from sklearn import linear_model


class LocalService:

    def __init__(self):
        self.model = linear_model.LinearRegression()

    def train(self, ds):
        X, y = ds
        self.model.fit(X, y)

    def predict(self, ds):
        r = self.model.predict(ds)
        print(r)
