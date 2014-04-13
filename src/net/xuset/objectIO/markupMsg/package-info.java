

/**
 * MarkupMsg is to objectIO as red blood cells are to the circulatory system. They are
 * the messages that get sent from point A to be point B.
 * 
 * <p>
 * A lot of the time these messages get sent across an I/O stream. In this case the
 * object itself cannot be sent over the stream. It must be broken down into a raw form
 * like a byte array or char array.
 * {@link net.xuset.objectIO.markupMsg.AsciiMsgParser AsciiMsgParser} does a good
 * job at converting
 * MarkupMsg objects into a stream friendly representation. AsciiMsgParser inherits the
 * {@link net.xuset.objectIO.markupMsg.MsgParser MsgParser} interface, as should any
 * object that converts a MarkupMsg to and
 * from a raw form.
 * </p>
 * 
 */

package net.xuset.objectIO.markupMsg;