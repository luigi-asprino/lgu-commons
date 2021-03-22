// -*-C++-*-

#ifndef PRANK_H
#define PRANK_H

#include <boost/graph/graph_concepts.hpp>
#include <boost/unordered_set.hpp>
#include <queue>
#include <boost/tuple/tuple.hpp> // for "tie"
#include <iosfwd>

/////////////////////////////////////////////////////////////////////
// pageRank
//
// it solves pageRank with the so called power method see
// @article{langville04,
// title = {{Deeper Inside PageRank}},
// author = {A.N. Langville and C.D. Meyer},
// journal = {Internet Mathematics},
// number = {3},
// pages = {335--380},
// volume = {1},
// year = {2004},
// }
//
// Note: it correctly handles dangling nodes

namespace ukb {

	namespace prank {

		//
		// a constant property map that always returns the same value
		//


		template<typename K, typename V>
		class constant_property_map
			: public boost::put_get_helper<V, constant_property_map<K, V> > {
		public:
			typedef K key_type;
			typedef V value_type;
			typedef V reference;
			typedef boost::readable_property_map_tag category;

			constant_property_map(V value) : store(value) {}

			inline value_type operator[](const key_type& v) const { return store; }
		private:
			V store;
		};

		////////////////////////////////
		//
		// Init out_coefs so that out_coefs[v] has the sum of weights of
		// out-edges.
		// Also, if out_coefs[u] == -1.0 thet u is isolated

		template<typename G, typename coefmap_t, typename wmap_t>
		size_t init_out_coefs(const G & g,
							  coefmap_t W,
							  wmap_t wmap) {

			typename graph_traits<G>::vertex_iterator v;
			typename graph_traits<G>::vertex_iterator end;
			boost::tie(v, end) = vertices(g);
			size_t N = 0;
			for (; v != end; ++v) {
				size_t i = 0;
				typename graph_traits<G>::out_edge_iterator e, e_end;
				boost::tie(e, e_end) = out_edges(*v, g);
				float total_w = 0.0;
				for(; e != e_end; ++e) {
					++i;
					total_w += wmap[*e];
				}
				if (i) {
					W[*v] = 1.0f / total_w;
					N++;
				} else {
					// See if isolated
					typename graph_traits<G>::in_edge_iterator ie, ie_end;
					boost::tie(ie, ie_end) = in_edges(*v, g);
					if (ie == ie_end) {
						W[*v] = -1.0;
					} else {
						W[*v] = 0.0;
						N++;
					}
				}
			}
			return N;
		}

		template<typename G, typename coefmap_t>
		size_t init_out_coefs(const G & g,
							  coefmap_t W) {
			typedef typename graph_traits<G>::edge_descriptor edge_descriptor;
			constant_property_map <edge_descriptor, float> cte_weight(1); // always return 1
			return init_out_coefs(g, W, cte_weight);
		}


		//
		// Apply one step of pageRank algorithm
		//

		template<typename G, typename ppvMap_t, typename wMap_t, typename map1_t, typename map2_t>
		float update_pRank(G & g,
						   std::pair<typename graph_traits<G>::vertex_iterator,
						   typename graph_traits<G>::vertex_iterator> V,
						   float damping,
						   ppvMap_t ppv_V,
						   const std::vector<float> & out_coef,
						   wMap_t & wmap,
						   const map1_t rank_map1,
						   map2_t rank_map2) {

			typedef typename graph_traits<G>::vertex_descriptor vertex_descriptor;

			typename graph_traits<G>::vertex_iterator v_it = V.first;
			typename graph_traits<G>::vertex_iterator end = V.second;

			float norm = 0.0;
			for (; v_it != end; ++v_it) {
				float rank=0.0;
				vertex_descriptor v(*v_it);
				if (-1.0 == out_coef[v]) continue;
				typename graph_traits<G>::in_edge_iterator e, e_end;
				boost::tie(e, e_end) = in_edges(v, g);
				for(; e != e_end; ++e) {
					vertex_descriptor u = source(*e, g);
					rank += rank_map1[u] * wmap[*e] * out_coef[u];
				}
				float dangling_factor = 0.0;
				if (0.0 == out_coef[v]) {
					// dangling link
					dangling_factor = damping * rank_map1[v];
				}
				rank_map2[v] = damping * rank + (dangling_factor + 1.0 - damping ) * ppv_V[v];
				norm += fabs(rank_map2[v] - rank_map1[v]);
			}
			return norm;
		}

		//
		// Initialize rank and iterate
		//

		template<typename G, typename ppvMap_t, typename wMap_t, typename map1_t, typename map2_t>
		void do_pageRank(G & g,
						 size_t N,
						 ppvMap_t ppv_V,
						 wMap_t & wmap,
						 map1_t rank_map1,
						 map2_t rank_map2,
						 int iterations,
						 float threshold,
						 float damping,
						 const std::vector<float> & out_coef) {

			if (N == 0) return;
			if (iterations == 0 && threshold == 0.0)
				throw std::runtime_error("prank error: iterations and threshold are set to zero!\n");
			if (!iterations) iterations = std::numeric_limits<int>::max();

			std::pair<typename graph_traits<G>::vertex_iterator,
					  typename graph_traits<G>::vertex_iterator> V = vertices(g);

			// Initialize rank_map1 appropriately
			{
				const float init_value = 1.0f/static_cast<float>(N);
				typename graph_traits<G>::vertex_iterator v = V.first;
				for (; v != V.second; ++v) {
					rank_map1[*v] = init_value;
				}
			}

			// Continue iterating until the termination condition is met

			bool to_map_2 = true;
			float residual = 0.0;
			while(iterations--) {
				// Update to the appropriate rank map
				if (to_map_2)
					residual = update_pRank(g, V, damping, ppv_V, out_coef, wmap, rank_map1, rank_map2);
				else
					residual = update_pRank(g, V, damping, ppv_V, out_coef, wmap, rank_map2, rank_map1);
				// The next iteration will reverse the update mapping
				to_map_2 = !to_map_2;
				if (residual < threshold) break;
			}

			// If we stopped after writing the latest results to rank_map2,
			// copy the results back to rank_map1 for the caller
			if (!to_map_2) {
				typename graph_traits<G>::vertex_iterator v = V.first;
				for (; v != V.second; ++v) {
					rank_map1[*v] = rank_map2[*v];
				}
			}
		}


		/////////////////////////////////////////////////////////////////
		// PageRank iteration
		//

		//
		// main entry point
		//

		template<typename G, typename ppvMap_t, typename wMap_t, typename map1_t, typename map2_t>
		void pageRank_iterate(G & g,
							  ppvMap_t ppv_V,
							  wMap_t & wmap,
							  map1_t rank_map1,
							  map2_t rank_map2,
							  int iterations,
							  float threshold,
							  float damping) {

			std::vector<float> out_coef(num_vertices(g), 0.0f);
			// Initialize out_coef
			size_t M = init_out_coefs(g, &out_coef[0], wmap);
			do_pageRank(g, M, ppv_V, wmap, rank_map1, rank_map2, iterations, threshold, damping, out_coef);
		}

		//
		// PageRank algorithm without weights
		//
		// main entry point
		//

		template<typename G, typename ppvMap_t, typename map1_t, typename map2_t>
		void pageRank_iterate_now(G & g,
								  ppvMap_t ppv_V,
								  map1_t rank_map1,
								  map2_t rank_map2,
								  int iterations,
								  float threshold,
								  float damping) {

			typedef typename graph_traits<G>::edge_descriptor edge_descriptor;
			constant_property_map <edge_descriptor, float> cte_weight(1); // always return 1
			pageRank_iterate(g, ppv_V, cte_weight, rank_map1, rank_map2, iterations, threshold, damping);
		}


		//
		// PageRank nibble
		//
		// This is achieved by the algorithm finding the approximate
		// personalized PageRank score of every node with respect to the seed
		// node. This implementation extends the original algorithm so that it
		// takes any personalized vector as input (in original formulation all
		// mass of the pv is concentrated in a single node, the seed node).
		//
		// See: Local Graph Partitioning using PageRank Vectors by R. Andersen, F. Chung and K. Lang
		// URL: http://www.math.ucsd.edu/~fan/wp/localpartition.pdf


		template<class G>
		void pageRank_nibble(G & g,
							 const std::vector<float> & ppv_map,
							 const std::vector<float> & out_coefs,
							 float damping,
							 float epsilon,
							 std::vector<float> & p) {

			typedef typename boost::graph_traits<G>::vertex_descriptor vertex_descriptor;
			typedef typename boost::graph_traits<G>::adjacency_iterator adjacency_iterator;
			typedef typename boost::graph_traits<G>::vertex_iterator vertex_iterator;

			boost::unordered_set<vertex_descriptor> S;
			std::queue<vertex_descriptor> Q;
			std::vector<float> r(ppv_map);
			std::fill(p.begin(), p.end(), 0.0f);

			vertex_iterator it, end;
			boost::tie(it, end) = vertices(g);
			for(; it != end; ++it) {
				if (r[*it] * out_coefs[*it] >= epsilon) {
					S.insert(*it); Q.push(*it);
				}
			}
			while(Q.size()) {
				vertex_descriptor u = Q.front();
				Q.pop();
				S.erase(u);
				do {
					// Push
					adjacency_iterator it, end;
					boost::tie(it, end) = adjacent_vertices(u, g);
					float pushVal = r[u];
					float putVal = damping * 0.5 * r[u];
					p[u] += (1.0 - damping) * pushVal;
					r[u] = putVal;
					for(; it != end; ++it) {
						r[*it] += putVal * out_coefs[u];
						if (r[*it] * out_coefs[*it] >= epsilon) {
							if (S.insert(*it).second) Q.push(*it);
						}
					}
				} while(r[u] * out_coefs[u] >= epsilon);
			}
		}

		// PageRank nibble, version described in the Phd
		//
		// the main difference is that this version uses a lazy random walk model


		template<class G>
		void pageRank_nibble_lazy(G & g,
								  const std::vector<float> & ppv_map,
								  const std::vector<float> & out_coefs,
								  float damping,
								  float epsilon,
								  std::vector<float> & p) {

			typedef typename boost::graph_traits<G>::vertex_descriptor vertex_descriptor;
			typedef typename boost::graph_traits<G>::adjacency_iterator adjacency_iterator;
			typedef typename boost::graph_traits<G>::vertex_iterator vertex_iterator;

			boost::unordered_set<vertex_descriptor> S;
			std::queue<vertex_descriptor> Q;
			std::vector<float> r(ppv_map);
			std::fill(p.begin(), p.end(), 0.0f);

			vertex_iterator it, end;
			boost::tie(it, end) = vertices(g);
			for(; it != end; ++it) {
				if (r[*it] * out_coefs[*it] >= epsilon) {
					S.insert(*it); Q.push(*it);
				}
			}
			while(Q.size()) {
				vertex_descriptor u = Q.front();
				Q.pop();
				S.erase(u);
				do {
					// Push
					adjacency_iterator it, end;
					boost::tie(it, end) = adjacent_vertices(u, g);
					float pushVal = r[u] - 0.5 * epsilon;
					float putVal = damping * (r[u] - 0.5 * epsilon) * out_coefs[u];
					p[u] += (1.0 - damping) * pushVal;
					r[u] = 0.5 * epsilon;
					for(; it != end; ++it) {
						r[*it] += putVal;
						if (r[*it] * out_coefs[*it] >= epsilon) {
							if (S.insert(*it).second) Q.push(*it);
						}
					}
				} while(r[u] * out_coefs[u] >= epsilon);
			}
		}

		template<class G>
		void pageRank_nibble_snap(G & g,
								  const std::vector<float> & ppv_map,
								  const std::vector<float> & out_coefs,
								  float damping,
								  float epsilon,
								  std::vector<float> & p) {

			typedef typename boost::graph_traits<G>::vertex_descriptor vertex_descriptor;
			typedef typename boost::graph_traits<G>::adjacency_iterator adjacency_iterator;
			typedef typename boost::graph_traits<G>::vertex_iterator vertex_iterator;

			boost::unordered_set<vertex_descriptor> S;
			std::queue<vertex_descriptor> Q;
			std::vector<float> r(ppv_map);
			std::fill(p.begin(), p.end(), 0.0f);

			vertex_iterator it, end;
			boost::tie(it, end) = vertices(g);
			for(; it != end; ++it) {
				if (r[*it] * out_coefs[*it] >= epsilon) {
					S.insert(*it); Q.push(*it);
				}
			}
			while(Q.size()) {
				vertex_descriptor u = Q.front();
				Q.pop();
				S.erase(u);
				do {
					// Push
					adjacency_iterator it, end;
					boost::tie(it, end) = adjacent_vertices(u, g);
					float pushVal = r[u] - 0.5 * epsilon * (end - it);
					float putVal = damping * (r[u] - 0.5 * epsilon); // (1-damping) * pushVal / (end-it);
					p[u] += (1.0 - damping) * pushVal;
					r[u] = 0.5 * epsilon * (end - it);
					for(; it != end; ++it) {
						r[*it] += putVal;
						if (r[*it] * out_coefs[*it] >= epsilon) {
							if (S.insert(*it).second) Q.push(*it);
						}
					}
				} while(r[u] * out_coefs[u] >= epsilon);
			}

		}

		/////////////////////////////////////////////////////////////////////////

		template<typename G, typename coefmap_t>
		void init_degree(const G & g,
						 coefmap_t In) {

			typename graph_traits<G>::vertex_iterator v, v_end;

			boost::tie(v, v_end) = vertices(g);
			for(; v != v_end; ++v) {
				In[*v] = in_degree(*v, g);
			}
		}
	}
}
#endif
