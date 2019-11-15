from gensim.test.utils import datapath
from gensim import utils
from gensim.utils import tokenize
import gensim.models
import logging
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


def takeSecond(elem):
    return elem[1]


class MyCorpus(object):
    
    """An interator that yields sentences (lists of str)."""

    def __iter__(self, path):
        corpus_path = datapath(path)
        for line in open(corpus_path):
            # assume there's one document per line, tokens separated by whitespace
            yield line.split(" ")


if __name__ == '__main__':
    
    sentences = MyCorpus()
    m = "/Users/lgu/Desktop/fn17_w2v_model"
    # model = gensim.models.Word2Vec(sentences=sentences, size=200, workers=5, window=10, sg=1, negative=15, iter=5)
    
    # model.save(m)
    
    model = gensim.models.Word2Vec.load(m)
    
    # print model["https://w3id.org/framester/framenet/abox/frame/Being_operational"]
    
    # print(model.most_similar(positive=["https://w3id.org/framester/framenet/abox/frame/Being_operational"], topn=5))
    
    a = ["frame:Killing", "frame:Transitive_action", "frame:Execution", "frame:Death", "frame:Smuggling"]
    
    # for e, s in model.most_similar(positive=["forever"], topn=100):
    #    print e, " ", s

    for e1 in a:
        b = [(e2, model.similarity(e1, e2))  for e2 in a if e2 <> e1]
        print b      
        b.sort(key=takeSecond, reverse=True)
        print b  
        
