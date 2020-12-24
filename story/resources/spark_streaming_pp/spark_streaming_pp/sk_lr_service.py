class SkLrService:
    def __init__(self, sc):
        self.sc = sc
        self.model_value = None

    def train(self):
        import numpy as np
        from sklearn import linear_model
        X = np.array([[1, 1], [1, 2], [2, 2], [2, 3]])
        y = np.dot(X, np.array([1, 2])) + 3  # [ 6  8  9 11]
        m = linear_model.LinearRegression()
        m.fit(X, y)
        self.model_value = self.sc.broadcast(m)

    def predict(self, ds):
        import numpy as np

        model = self.model_value.value

        def transform(v):
            values = []
            for x in range(len(v)):
                values.append(v[x])
            label = values[0]
            features = values[1:]
            return label, model.predict([np.array(features)])

        ds.map(lambda x: transform(x)).pprint()
