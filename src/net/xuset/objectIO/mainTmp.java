package net.xuset.objectIO;

import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParser;
import net.xuset.objectIO.netObj.NetClass;
import net.xuset.objectIO.netObj.NetComp;
import net.xuset.objectIO.netObj.NetVar;

/**
 * Created by xuset on 7/24/14.
 */
public class mainTmp {

    public static MsgParser parser = new AsciiMsgParser();

    public static class Entity {

        @NetVar(recursive = true)
        Entity entity = null;

        @NetVar
         int x = 0;

        @NetVar
         Integer y = 0;

        @Override
        public String toString() {
            return x + " : " + y + " : " + String.valueOf(entity);
        }
    }

    public static class Wall extends Entity {

        @NetVar
        String spriteId = "wall";

        @Override
        public String toString() {
            return super.toString() + " : " + spriteId;
        }
    }

    public static void main(String[] args) {
        Entity e = new Wall();
        e.entity = new Entity();
        NetClass comp = new NetClass("1231313", e);

        MarkupMsg saveState = comp.serializeToMsg();
        System.out.println(e);

        e.x = 33; e.y = 44; ((Wall)e).spriteId = "ball";
        e.entity.x = 1; e.entity.y = 2;
        System.out.println(e);

        comp.deserializeMsg(saveState);
        System.out.println(e);

    }
}
