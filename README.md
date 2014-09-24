# amqp-stream

A scalaz-stream way to talk to RabbitMQ

## Testing
The testing process requires a connection to a RabbitMQ server.  If you have one running locally on the 
standard port with a guest user, then you don't have to do anything.  However, if you have it running
somewhere else, you'll need to pass in the connection settings as an environment variable:

    RABBITMQ_URL=amqp://user:pass@somewhere-non-local/%2F ./sbt

## Releasing
We're using the fine `sbt-release` plugin for releasing new versions.  Just type:

    release

And follow the prompts for releasing nirvana upon the world!
