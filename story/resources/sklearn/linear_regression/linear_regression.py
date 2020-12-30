import numpy as np
from sklearn import linear_model
from joblib import dump


def run():
    save = False

    # Prepare data.
    X = np.array([
        [1, 1],  # 6
        [1, 2],  # 8
        [2, 2],  # 9
        [2, 3]  # 11
    ])
    y = np.dot(X, np.array([1, 2])) + 3  # [ 6  8  9 11], y = 1 * x_0 + 2 * x_1 + 3
    # Create model.
    model = linear_model.LinearRegression()
    # Train.
    model.fit(X, y)
    # Predict.
    v1 = model.predict(np.array([[3, 5]]))
    v2 = model.predict(np.array([[4, 6]]))
    # Results.
    print(v1)  # [16.]
    print(v2)  # [19.]

    if save:
        dump(model, "model.data")


def main():
    run()


if __name__ == "__main__":
    main()
