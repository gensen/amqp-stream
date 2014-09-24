package com.gs.stream

import com.rabbitmq.client.{Connection, ConnectionFactory, Channel, MessageProperties, AMQP}
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.stream.Process._
import scalaz.stream.Cause._
import scodec.bits.ByteVector

/**
 * We're going to create a RabbitMQ driver for Scalaz Streams that
 * connects and then creates an Exchange, allowing Scalaz to pull
 * message by message from the queue.
 */

package object amqp {
  /**
   * Process that reads from a RabbitMQ Queue using a Channel until that Queue is empty
   */
  def read(ch: Channel, queueName: String): Process[Task, ByteVector] = Process.repeatEval( Task.delay {
    Option(ch.basicGet(queueName, true))
      .map(_.getBody)
      .map(ByteVector(_))
      .getOrElse(throw Terminated(End))
  })

  /**
   * Process that writes messages to a RabbitMQ exchange using the provided routing key
   */
  def write(ch: Channel, exchangeName: String, routingKey: String, messageProperties: AMQP.BasicProperties = MessageProperties.BASIC): Sink[Task, ByteVector] = {
    def setup: Task[Channel] = Task.delay {
      try {
        ch.exchangeDeclarePassive(exchangeName)
        ch
      } catch {
        case e: java.io.IOException => throw Terminated(Error(e))
      }
    }
    await(setup)(ch => Process.repeatEval(Task.delay { (msg: ByteVector) => Task.delay { ch.basicPublish(exchangeName, routingKey, messageProperties, msg.toArray)}}))
  }
}
