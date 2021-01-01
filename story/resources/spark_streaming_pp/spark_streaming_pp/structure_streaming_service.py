from pyspark.ml.regression import LinearRegression


# https://runawayhorse001.github.io/LearningApacheSpark/regression.html

class StructuredStreamingService:
    def __init__(self):
        self.service = LinearRegression(maxIter=10, regParam=0.01)
        self.model = None

    def train(self, ds):
        self.model = self.service.fit(ds)

    def predict(self, ds):
        transformed_ds = self.model.transform(ds)
        q = transformed_ds.select("label", "prediction").writeStream.format("console").start()
        return q
