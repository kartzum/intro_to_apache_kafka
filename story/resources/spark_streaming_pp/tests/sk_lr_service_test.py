import unittest
import warnings

from time import sleep

from pyspark import SparkContext
from pyspark.streaming import StreamingContext

from spark_streaming_pp import sk_lr_service


class RunTest(unittest.TestCase):
    def test_run(self):
        sc, ssc = self.init()

        service = sk_lr_service.SkLrService(sc)

        service.train()

        p1 = sc.parallelize([(1.0, 3.0, 5.0)])
        p2 = sc.parallelize([(2.0, 4.0, 6.0)])

        service.predict(ssc.queueStream([p1, p2]))

        ssc.start()

        sleep(3)

        ssc.stop(stopSparkContext=True, stopGraceFully=True)

        # (1.0, array([16.]))
        # (2.0, array([19.]))

    def init(self):
        sc = SparkContext()
        ssc = StreamingContext(sc, 1)
        sc.setLogLevel("ERROR")
        return sc, ssc

    def setUp(self):
        warnings.filterwarnings("ignore", category=ResourceWarning)
        warnings.filterwarnings("ignore", category=DeprecationWarning)
        warnings.filterwarnings("ignore", category=RuntimeWarning)
