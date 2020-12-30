import unittest
import numpy as np

from spark_streaming_pp import local_service


class RunTest(unittest.TestCase):
    def test_run(self):
        # Prepare data.
        X = np.array([
            [1, 1],  # 6
            [1, 2],  # 8
            [2, 2],  # 9
            [2, 3]  # 11
        ])
        y = np.dot(X, np.array([1, 2])) + 3  # [ 6  8  9 11], y = 1 * x_0 + 2 * x_1 + 3
        # Create model and train.
        service = local_service.LocalService()
        service.train((X, y))
        # Predict and results.
        service.predict(np.array([[3, 5]]))
        service.predict(np.array([[4, 6]]))
        # [16.]
        # [19.]


if __name__ == "__main__":
    unittest.main()
