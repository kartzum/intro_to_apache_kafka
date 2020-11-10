import unittest
from ..mq import factory


class TestSum(unittest.TestCase):

    def test_sum(self):
        self.assertEqual(factory.s(2, 3), 5, "Should be 5")


if __name__ == "__main__":
    unittest.main()
