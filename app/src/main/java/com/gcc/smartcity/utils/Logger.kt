package com.gcc.smartcity.utils

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.*

object Logger {

    /**
     * Assign this value to [Logger.LOG_LEVEL] to disable logs
     * completely.
     */
    private val LOG_LEVEL_DISABLED = 0x8


    /**
     * LOG_LEVEL by default is set to INFO level.
     * This enables the following logs - INFO, WARN, ERROR, ASSERT
     */
    //    private static final int LOG_LEVEL = LOG_LEVEL_DISABLED;
    private val LOG_LEVEL = Log.DEBUG

    /**
     * Enables logging to file in SD card. Disabled
     * by default since its costly. Use only when its
     * necessary.
     */
    private val FILE_LOG = false


    /**
     * Holds the file instance to which we write the logs.
     * This is to avoid creating several file instances each time a log
     * is written
     */
    private var s_LogFile: File? = null


    /**
     * The name of the file to which Logs will be written to.
     * Can be changed to represent logs from each application
     */
    private val LOG_FILE_NAME = "Logger.txt"

    /**
     * The maximum file size log files are allowed to be written
     */
    private val MAX_LOG_FILE_SIZE = (100 * 1024 * 1024).toLong()    // 100 MB

    /**
     * The default log tag to be used if not provided. Set this value if
     * a default Log tag is required.
     */
    private val DEFAULT_LOG_TAG = "SmartCity"

    /**
     * Funtion to check SD card is mounted and is available for writing.
     * This function does not verify if the permission WRITE_EXTERNAL_STORAGE has
     * been granted in the application's manifest.
     *
     * @return TRUE, if we can write to SD card. False, if otherwise.
     */
    private val isSdcardMounted: Boolean
        get() {
            val state = Environment.getExternalStorageState()

            if (state != Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY) {
                Log.i(DEFAULT_LOG_TAG, "SDCard is NOT mounted. Cannot write Logs to FILE")
                return false
            }

            return true
        }

    private// Maximum size for Log file has been breached. Clear the logs and start anew.
    // Overwrite
    // We can continue appending
    // Append
    val printStream: PrintStream?
        @Synchronized get() {

            var printStream: PrintStream? = null
            if (s_LogFile == null) {
                s_LogFile = File(Environment.getExternalStorageDirectory(), LOG_FILE_NAME)
            }

            try {
                if (s_LogFile!!.length() > MAX_LOG_FILE_SIZE) {
                    printStream = PrintStream(s_LogFile!!)
                } else {
                    val outputFile = FileOutputStream(s_LogFile!!, true)
                    printStream = PrintStream(outputFile)
                }
            } catch (e: FileNotFoundException) {
                Log.e(DEFAULT_LOG_TAG, "Exception in getting stream: " + e.message)
            }

            return printStream
        }

    /**
     * @return Returns time in a format that can be printed on the file for each log statements.
     */
    private val time: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            return (calendar.get(Calendar.DATE).toString() + "/"
                    + calendar.get(Calendar.MONTH) + "/"
                    + calendar.get(Calendar.YEAR) + " "
                    + calendar.get(Calendar.HOUR) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND))
        }

    /**
     * Utility function that prints the message to Console if [Logger.LOG_LEVEL]
     * is lesser than [Log.INFO]
     *
     *
     *
     * If [Logger.FILE_LOG] is true, the logs are written to file [Logger.LOG_FILE_NAME]
     * in SD card.
     *
     * @param tag     Tag to be written to console.
     * @param message Message to be written to console.
     */
    fun i(tag: String, message: String) {
        if (canLog(Log.INFO)) {
            Log.i(tag, message)

            if (FILE_LOG) {
                writeLogToFile("INFO", tag, message)
            }
        }
    }

    /**
     * Convenience function for [Logger.i] using
     * [Logger.DEFAULT_LOG_TAG] for tag.
     *
     * @param message Message to be written to console.
     */
    fun i(message: String) {
        i(DEFAULT_LOG_TAG, message)
    }

    /**
     * Convenience function for [Logger.v] using
     * [Logger.DEFAULT_LOG_TAG] for tag.
     *
     * @param message Message to be written to console.
     */
    fun v(message: String) {
        v(DEFAULT_LOG_TAG, message)
    }

    /**
     * Utility function that prints the message to Console if [Logger.LOG_LEVEL]
     * is lesser than [Log.VERBOSE]
     *
     *
     *
     * If [Logger.FILE_LOG] is true, the logs are written to file [Logger.LOG_FILE_NAME]
     * in SD card.
     *
     * @param tag     Tag to be written to console.
     * @param message Message to be written to console.
     */
    fun v(tag: String, message: String) {
        if (canLog(Log.VERBOSE)) {
            Log.v(tag, message)

            if (FILE_LOG) {
                writeLogToFile("VERBOSE", tag, message)
            }
        }
    }

    /**
     * Convenience function for [Logger.d] using
     * [Logger.DEFAULT_LOG_TAG] for tag.
     *
     * @param message Message to be written to console.
     */
    fun d(message: String) {
        d(DEFAULT_LOG_TAG, message)
    }

    /**
     * Utility function that prints the message to Console if [Logger.LOG_LEVEL]
     * is lesser than [Log.DEBUG]
     *
     *
     *
     * If [Logger.FILE_LOG] is true, the logs are written to file [Logger.LOG_FILE_NAME]
     * in SD card.
     *
     * @param tag     Tag to be written to console.
     * @param message Message to be written to console.
     */
    fun d(tag: String, message: String) {
        if (canLog(Log.DEBUG)) {
            Log.d(tag, message)

            if (FILE_LOG) {
                writeLogToFile("DEBUG", tag, message)
            }
        }
    }

    /**
     * Convenience function for [Logger.w] using
     * [Logger.DEFAULT_LOG_TAG] for tag.
     *
     * @param message Message to be written to console.
     */
    fun w(message: String) {
        w(DEFAULT_LOG_TAG, message)
    }

    /**
     * Utility function that prints the message to Console if [Logger.LOG_LEVEL]
     * is lesser than [Log.WARN]
     *
     *
     *
     * If [Logger.FILE_LOG] is true, the logs are written to file [Logger.LOG_FILE_NAME]
     * in SD card.
     *
     * @param tag     Tag to be written to console.
     * @param message Message to be written to console.
     */
    fun w(tag: String, message: String) {
        if (canLog(Log.WARN)) {
            Log.w(tag, message)

            if (FILE_LOG) {
                writeLogToFile("WARN", tag, message)
            }
        }
    }

    /**
     * Convenience function for [Logger.e] using
     * [Logger.DEFAULT_LOG_TAG] for tag.
     *
     * @param message Message to be written to console.
     */
    fun e(message: String) {
        e(DEFAULT_LOG_TAG, message)
    }

    /**
     * Utility function that prints the message to Console if [Logger.LOG_LEVEL]
     * is lesser than [Log.ERROR]
     *
     *
     *
     * If [Logger.FILE_LOG] is true, the logs are written to file [Logger.LOG_FILE_NAME]
     * in SD card.
     *
     * @param tag     Tag to be written to console.
     * @param message Message to be written to console.
     */
    fun e(tag: String, message: String) {
        if (canLog(Log.ERROR)) {
            Log.e(tag, message)

            if (FILE_LOG) {
                writeLogToFile("ERROR", tag, message)
            }
        }
    }

    /**
     * Convenience function for [Logger.a] using
     * [Logger.DEFAULT_LOG_TAG] for tag.
     *
     * @param message Message to be written to console.
     */
    fun a(message: String) {
        a(DEFAULT_LOG_TAG, message)
    }

    /**
     * Utility function that prints the message to Console if [Logger.LOG_LEVEL]
     * is lesser than [Log.ASSERT]
     *
     *
     *
     * If [Logger.FILE_LOG] is true, the logs are written to file [Logger.LOG_FILE_NAME]
     * in SD card.
     *
     * @param tag     Tag to be written to console.
     * @param message Message to be written to console.
     * @see Log.wtf
     */
    fun a(tag: String, message: String) {
        if (canLog(Log.ASSERT)) {
            Log.wtf(tag, message)

            if (FILE_LOG) {
                writeLogToFile("ASSERT", tag, message)
            }
        }
    }

    fun exc(e: Exception) {
        if (canLog(Log.ERROR)) {
            e.printStackTrace()

            if (FILE_LOG) {
                writeStackTraceToFile(e)
            }
        }
    }

    /**
     * Determines if the current log level is allowed for writing to console
     *
     * @param logLevel The log level to be tested for eligibility
     * @return True, if the logging for the level is allowed.
     * False, if otherwise.
     */
    private fun canLog(logLevel: Int): Boolean {
        return logLevel >= LOG_LEVEL
    }

    /**
     * Prints the message to file specified by [Logger.LOG_FILE_NAME] if SD card is mounted
     * and available for writing.
     *
     * @param logType The type of log to be written. One of "VERBOSE", "DEBUG", "INFO", "WARN", "ERROR", "ASSERT"
     * @param tag     The tag that was designed for console. Used to indicate application or class
     * @param message The log message to be written
     */
    @Synchronized
    private fun writeLogToFile(logType: String, tag: String, message: String) {
        if (isSdcardMounted) {
            var printStream: PrintStream? = null

            try {
                printStream = printStream

                val msg =
                    time + ": " + logType + "/" + tag + ": " + message + System.getProperty("line.separator")
                printStream!!.write(msg.toByteArray())
                printStream.flush()

            } catch (e: Exception) {
                Log.i(DEFAULT_LOG_TAG, "Exception in writing Logs to FILE: " + e.message)
            } finally {
                // Close the stream. Do not enable file logs unless absolutely necessary.
                // This is too costly.
                printStream?.close()
            }
        }
    }

    @Synchronized
    private fun writeStackTraceToFile(e: Exception) {
        if (isSdcardMounted) {
            var printStream: PrintStream? = null

            try {
                printStream = printStream
                e.printStackTrace(printStream!!)
            } catch (ex: Exception) {
                Log.i(DEFAULT_LOG_TAG, "Exception in writing stack trace to FILE: " + ex.message)
            } finally {
                // Close the stream. Do not enable file logs unless absolutely necessary.
                // This is too costly.
                printStream?.close()
            }

        }
    }

}
