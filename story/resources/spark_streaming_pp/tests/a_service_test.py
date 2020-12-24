import unittest
import warnings

from time import sleep

from pyspark import SparkContext
from pyspark.streaming import StreamingContext

from spark_streaming_pp import a_service


class RunTest(unittest.TestCase):
    def test_run(self):
        service = a_service.AService([0.0, 0.0, 0.0])

        self.train(service)

        self.predict(service)

    def train(self, service):
        sc, ssc = self.init()

        t1 = sc.parallelize([(1.0, 0.1, 0.2, 0.3)])
        t2 = sc.parallelize([(2.0, 0.0, 0.0, 0.3)])
        t3 = sc.parallelize([(3.0, 0.0, 0.2, 0.3)])

        service.train(ssc.queueStream([t1, t2, t3]))

        ssc.start()

        sleep(2)

        ssc.stop(stopSparkContext=True, stopGraceFully=True)

    def predict(self, service):
        sc, ssc = self.init()

        p1 = sc.parallelize([(1.0, 0.1, 0.2, 0.3)])
        p2 = sc.parallelize([(2.0, 0.3, 0.1, 0.2)])

        service.predict(ssc.queueStream([p1, p2]))

        ssc.start()

        sleep(2)

        ssc.stop(stopSparkContext=True, stopGraceFully=True)

    def init(self):
        sc = SparkContext()
        ssc = StreamingContext(sc, 1)
        sc.setLogLevel("ERROR")
        return sc, ssc

    def setUp(self):
        warnings.filterwarnings("ignore", category=ResourceWarning)
        warnings.filterwarnings("ignore", category=DeprecationWarning)


if __name__ == "__main__":
    unittest.main()
