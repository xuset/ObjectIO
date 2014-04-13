/**
 * A {@link net.xuset.objectIO.netObj.NetObject NetObject} is used for converting a
 * MarkupMsg object into something
 * meaningful. Objects that inherit the NetObject interface provide means for
 * serializing itself into a MarkupMsg object and then deserializing a message.
 * 
 * <p>
 * The {@link net.xuset.objectIO.netObj.NetVar NetVar} class stores a variable.
 * When the variable changes, an update
 * MarkupMsg message can be created then sent to other Connection instances so those
 * connections can know what the new value is.
 * </p>
 */

package net.xuset.objectIO.netObj;
