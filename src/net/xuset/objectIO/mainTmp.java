package net.xuset.objectIO;

import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParser;
import net.xuset.objectIO.netObj.NetComp;
import net.xuset.objectIO.netObj.NetPrim;

/**
 * Created by xuset on 7/24/14.
 */
public class mainTmp {

    public static MsgParser parser = new AsciiMsgParser();

    public static class Entity {

        @NetPrim
        boolean isVisible = false;

        @NetPrim
         int x = 0;

        @NetPrim
         Integer y = 0;

        @Override
        public String toString() {
            return x + " : " + y + " : " + isVisible;
        }
    }

    public static class Wall extends Entity {

        @NetPrim
        String spriteId = "wall";

        @Override
        public String toString() {
            return super.toString() + " : " + spriteId;
        }
    }

    public static void main(String[] args) {
        Entity e = new Wall();
        NetComp comp = new NetComp("1231313", e);

        MarkupMsg saveState = comp.serializeToMsg();
        System.out.println(e);

        e.x = 33; e.y = 44; e.isVisible = true; ((Wall)e).spriteId = "ball";
        System.out.println(e);

        comp.deserializeMsg(saveState);
        System.out.println(e);

    }
}
