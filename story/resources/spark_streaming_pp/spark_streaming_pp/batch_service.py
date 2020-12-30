from pyspark.ml.regression import LinearRegression


class BatchService:
    def __init__(self):
        self.service = LinearRegression(maxIter=10, regParam=0.01)
        self.model = None

    def train(self, ds):
        self.model = self.service.fit(ds)

    def predict(self, ds):
        prediction = self.model.transform(ds)
        result = prediction.select("label", "prediction").collect()
        for row in result:
            print(f"{row.label}, {row.prediction}")
