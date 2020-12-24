import numpy as np
from sklearn import datasets, linear_model
from sklearn.metrics import mean_squared_error
# import pickle
from joblib import dump


def simple():
    save = False

    X = np.array([[1, 1], [1, 2], [2, 2], [2, 3]])
    # y = 1 * x_0 + 2 * x_1 + 3
    y = np.dot(X, np.array([1, 2])) + 3  # [ 6  8  9 11]
    model = linear_model.LinearRegression()
    model.fit(X, y)

    print(model.predict(np.array([[3, 5]])))  # [16.]
    print(model.predict(np.array([[4, 6]])))  # [19.]

    if save:
        dump(model, "model.data")


def d():
    save = False

    d_X, d_y = datasets.load_diabetes(return_X_y=True)

    d_X_ = d_X[:, np.newaxis, 2]

    d_X_train = d_X_[:-20]
    d_X_test = d_X_[-20:]

    d_y_train = d_y[:-20]
    d_y_test = d_y[-20:]

    model = linear_model.LinearRegression()

    model.fit(d_X_train, d_y_train)

    d_y_pred = model.predict(d_X_test)

    mse = mean_squared_error(d_y_test, d_y_pred)

    print(model.coef_)  # [938.23786125]
    print(mse)  # 2548.0723987259703

    if save:
        # with open("model.data", 'wb') as fl:
        #    pickle.dumps(model, fl)
        dump(model, "model.data")


def main():
    simple()
    # d()


if __name__ == "__main__":
    main()
