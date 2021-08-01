package free.practice.log

import net.logstash.logback.marker.MapEntriesAppendingMarker
import org.slf4j.LoggerFactory

abstract class Logger { self =>
  def isDebugEnable: Boolean
  def debug(s: => String): Logger
  def debug(s: => String, t: Throwable): Logger
  final def debug[A: LogEncoder](a: A): Logger = {
    LogEncoder[A].apply(a) match {
      case (s, Some(t)) => self.debug(s, t)
      case (s, None) => self.debug(s)
    }
    self
  }
  def info(s: => String): Logger
  def info(s: => String, t: Throwable): Logger
  final def info[A: LogEncoder](a: A): Logger = {
    LogEncoder[A].apply(a) match {
      case (s, Some(t)) => self.info(s, t)
      case (s, None) => self.info(s)
    }
    self
  }
  def warn(s: => String): Logger
  def warn(s: => String, t: Throwable): Logger
  final def warn[A: LogEncoder](a: A): Logger = {
    LogEncoder[A].apply(a) match {
      case (s, Some(t)) => self.warn(s, t)
      case (s, None) => self.warn(s)
    }
    self
  }
  def error(s: => String): Logger
  def error(s: => String, t: Throwable): Logger
  final def error[A: LogEncoder](a: A): Logger = {
    LogEncoder[A].apply(a) match {
      case (s, Some(t)) => self.error(s, t)
      case (s, None) => self.error(s)
    }
    self
  }

  def isTraceEnable: Boolean
  def trace(s: => String): Logger
  def trace(s: => String, t: Throwable): Logger
  final def trace[A: LogEncoder](a: A): Logger = {
    LogEncoder[A].apply(a) match {
      case (s, Some(t)) => self.trace(s, t)
      case (s, None) => self.trace(s)
    }
    self
  }

  def ctx(key: String, value: Any): Logger

  final def ctx[A](value: A)(using c: LogContextEncoder[A]): Logger =
    c(value).foldLeft(this) { case (a, (k, v)) => a.ctx(k, v) }
}

object Logger {
  private def apply(markers: Map[String, String], l: org.slf4j.Logger): Logger = new Logger {
    import scala.collection.JavaConverters._

    private[this] lazy val m = new MapEntriesAppendingMarker(markers.asJava)


    final def isDebugEnable: Boolean = l.isDebugEnabled

    final def debug(s: => String): Logger = {
      if (l.isDebugEnabled) l.debug(m, s)
      this
    }

    final def debug(s: => String, t: Throwable): Logger = {
      if (l.isDebugEnabled) l.debug(m, s, t)
      this
    }

    final def info(s: => String): Logger = {
      if (l.isInfoEnabled) l.info(m, s)
      this
    }

    final def info(s: => String, t: Throwable): Logger = {
      if (l.isInfoEnabled) l.info(m, s, t)
      this
    }

    final def warn(s: => String): Logger = {
      if (l.isWarnEnabled) l.warn(m, s)
      this
    }

    final def warn(s: => String, t: Throwable): Logger = {
      if (l.isWarnEnabled) l.warn(m, s, t)
      this
    }

    final def error(s: => String): Logger = {
      if (l.isErrorEnabled) l.error(m, s)
      this
    }

    final def error(s: => String, t: Throwable): Logger = {
      if (l.isErrorEnabled) l.error(m, s, t)
      this
    }

    override def isTraceEnable: Boolean = l.isTraceEnabled

    final def trace(s: => String): Logger = {
      if (l.isTraceEnabled()) l.trace(m, s)
      this
    }

    final def trace(s: => String, t: Throwable): Logger = {
      if (l.isTraceEnabled) l.trace(m, s, t)
      this
    }

    final def ctx(key: String, value: Any): Logger = apply(markers.updated(key, value.toString), l)
  }

  def apply(l: org.slf4j.Logger): Logger = apply(Map.empty, l)
  def apply(c: Class[_]): Logger = apply(LoggerFactory.getLogger(c))
  def apply(s: String): Logger = apply(LoggerFactory.getLogger(s))
}