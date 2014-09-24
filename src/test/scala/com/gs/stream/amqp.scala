package com.gs.stream

import org.scalacheck.Prop._
import org.scalacheck.Properties

import java.nio.ByteBuffer
import com.rabbitmq.client.{Connection, ConnectionFactory}
import scala.concurrent.duration._
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.stream.Cause._
import scalaz.stream.Process._
import scalaz.stream.Process.Halt
import scalaz.stream.ReceiveY._
import java.util.concurrent.ScheduledExecutorService
import scodec.bits.ByteVector

object AmqpSpec extends Properties("amqp") {
  // simply connect to the server, send a message and get back what was sent
  property("read-write") = secure {
    val factory = new ConnectionFactory
    for { uri <- sys.env.get("RABBITMQ_URL") } factory.setUri(uri)
    val conn = factory.newConnection
    val ch = conn.createChannel
    val q = ch.queueDeclare.getQueue
    val e = "amq.direct"
    ch.queueBind(q, e, q)

    val msg = ByteVector("Hello world".getBytes("UTF-8"))

    val clientGot = try {
      Process.constant(msg).take(1).to(amqp.write(ch, e, q)).run.run
      amqp.read(ch, q).chunkAll.runLast.run.getOrElse(Vector[ByteVector]())
    } finally {
      conn.close
    }

    (clientGot.size == 1)   :| "client didn't get anything" &&
    (clientGot.head == msg) :| "client didn't get what it sent"
  }
}
