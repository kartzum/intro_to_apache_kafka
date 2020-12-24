# intro_to_apache_kafka

## Starting

### Resources
* [quickstart](https://kafka.apache.org/quickstart)
* [kafka-tutorials](https://kafka-tutorials.confluent.io/)
* [Introducing the Kafka Consumer: Getting Started with the New Apache Kafka 0.9 Consumer Client](https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/)
* [kafka-the-definitive](https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html)
* [kafka-architecture](https://data-flair.training/blogs/kafka-architecture/)

## Introduction 

### Clients

#### Kafka Java Client
* [java](https://docs.confluent.io/current/clients/java.html)

#### Kafka Python Client
* [python](https://docs.confluent.io/current/clients/python.html)

```
pip install confluent-kafka
```

```
conda install -c conda-forge python-confluent-kafka
```

## Consumer
See: fs/consumer/Common, fs/python/ckp/consumer

## Producer
See: fs/producer/Common, fs/python/ckp/producer

## Simple Service and Testing in java
See: k-service, SenderService, SenderServiceTest

## Streams
* See: WordCount/WordCountTest.

## Plain sources
* See: k-plain-sources.

## Spark. Spark+Python Basic
* See: spark_python_basic, spark-scala-basic
* See: spark-streaming-pr, spark_streaming_pp

### Spark Streaming
* https://scikit-learn.org/stable/auto_examples/linear_model/plot_ols.html
* https://github.com/apache/spark/blob/master/examples/src/main/python/streaming/queue_stream.py
* https://github.com/apache/spark/blob/master/examples/src/main/python/mllib/streaming_linear_regression_example.py

### How to prepare
* Install virtual environment with python 3.7
* Add: numpy, scikit-learn
* Download spark: https://spark.apache.org/downloads.html

### How to run python tests in IDE
* PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9-src.zip;SPARK_HOME=/Users/$USER/Documents/spark/spark-3.0.1-bin-hadoop2.7
* Add spark classes to IDE

### Resources
* [kafka-for-testing](https://gist.github.com/asmaier/6465468)
* [creating-first-apache-kafka-consumer-application](https://kafka-tutorials.confluent.io/creating-first-apache-kafka-consumer-application/kafka.html)
* [Подводные камни тестирования Kafka Streams](https://habr.com/ru/company/jugru/blog/499408/)
* [IoT там, где вы не ждали. Разработка и тестирование (часть 2)](https://habr.com/ru/company/jugru/blog/502898/)
* [Интеграционное тестирование в SpringBoot с TestContainers-стартером](https://habr.com/ru/company/otus/blog/514270/)
* [A Unit Testing Practitioner's Guide to Everyday Mockito](https://www.toptal.com/java/a-guide-to-everyday-mockito)
* [test-kafka-based-applications](https://medium.com/test-kafka-based-applications/https-medium-com-testing-kafka-based-applications-85d8951cec43)

## Processing Guarantees
* [Processing Guarantees](https://docs.confluent.io/current/streams/concepts.html#streams-concepts-processing-guarantees)
* [exactly-once-semantics](https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/?_ga=2.249178511.948638971.1604823182-338978087.1602507638)

## Testing
* [relative-imports-and-unittests](https://comeroutewithme.com/2018/02/24/python-relative-imports-and-unittests-derp/)

## Resources
* [kafka-streams-examples](https://github.com/confluentinc/kafka-streams-examples)
* [Kafka и микросервисы: обзор](https://habr.com/ru/company/avito/blog/465315/)
* [Apache Kafka для чайников](https://habr.com/ru/post/496182/)
* [RabbitMQ против Kafka: два разных подхода к обмену сообщениями](https://habr.com/ru/company/itsumma/blog/416629/)
* [Apache Kafka + Spring Boot: Hello, microservices](https://habr.com/ru/post/440400/)
* [Spring Boot + Apache Kafka и SSL в Docker контейнере](https://habr.com/ru/post/505720/)
* [О стримах и таблицах в Kafka и Stream Processing, часть 1](https://habr.com/ru/company/skbkontur/blog/353204/)
* [Apache Kafka и потоковая обработка данных с помощью Spark Streaming](https://habr.com/ru/post/451160/)
* [Семантика exactly-once в Apache Kafka](https://habr.com/ru/company/badoo/blog/333046/)
* [kafka-python](https://kafka-python.readthedocs.io/en/master/index.html)
* [public-apis](https://github.com/public-apis/public-apis)
* [chroniclingamerica.loc.gov](https://chroniclingamerica.loc.gov/about/api/#link)
* [geocode.xyz](https://geocode.xyz/api)

## imdb

### Resources
* [IMDb Datasets](https://www.imdb.com/interfaces/)
* [imdb-extensive-dataset](https://www.kaggle.com/stefanoleone992/imdb-extensive-dataset)
* [hollywood-movies-visualization-recommender-system](https://www.kaggle.com/rifkyahmadsaputra/hollywood-movies-visualization-recommender-system)
* [imdb-search](https://www.imdb.com/search/title/?release_date=2019-01-20,2019-01-21&countries=us)