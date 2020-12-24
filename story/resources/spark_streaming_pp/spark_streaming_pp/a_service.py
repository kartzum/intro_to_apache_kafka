from pyspark.mllib.regression import StreamingLinearRegressionWithSGD
from pyspark.mllib.linalg import Vectors
from pyspark.mllib.regression import LabeledPoint


class AService:

    def __init__(self, initial_weights):
        self.model = StreamingLinearRegressionWithSGD(stepSize=0.01, miniBatchFraction=0.5)
        self.model.setInitialWeights(initial_weights)

    def train(self, ds):
        def transform(v):
            values = []
            for x in range(len(v)):
                values.append(v[x])
            label = values[0]
            features = values[1:]
            return LabeledPoint(label, Vectors.dense(features))

        ds_labeled = ds.map(lambda x: transform(x))

        self.model.trainOn(ds_labeled)

    def predict(self, ds):
        def transform(v):
            values = []
            for x in range(len(v)):
                values.append(v[x])
            label = values[0]
            features = values[1:]
            return label, Vectors.dense(features)

        ds_labeled = ds.map(lambda x: transform(x))

        self.model.predictOnValues(ds_labeled).pprint()
