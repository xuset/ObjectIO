package net.xuset.objectIO;

import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParser;
import net.xuset.objectIO.netObj.NetClass;
import net.xuset.objectIO.netObj.list.ElementFactory;
import net.xuset.objectIO.netObj.list.NetArrayList;
import net.xuset.objectIO.netObj.NetVar;
import net.xuset.objectIO.netObj.list.NetList;

import java.util.List;

/**
 * Created by xuset on 7/24/14.
 */
public class mainTmp {

    public static MsgParser parser = new AsciiMsgParser();

    @NetVar
    public static class Entity {

        @NetVar
        NetList<Integer> score = new NetArrayList<Integer>("id", new ElementCreator());

        @NetVar
         int x = 0;

        @NetVar
         Integer y = 0;

        @Override
        public String toString() {
            return x + " : " + y + " : " + score;
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

    private static class ElementCreator implements ElementFactory<Integer> {

        @Override
        public Integer create() {
            return new Integer(0);
        }
    }

    public static void main(String[] args) {
        /*Entity e = new Wall();
        e.entity = new Wall();
        NetClass comp = new NetClass("1231313", e);

        MarkupMsg saveState = comp.serializeToMsg();
        System.out.println(e);

        change(e);
        System.out.println(e);

        comp.deserializeMsg(saveState);
        System.out.println(e);*/
        Entity e = new Entity();
        NetClass netClass = new NetClass("id", e);
        for (int i = 0; i < 10; i++) {
            e.score.add(i);
        }

        System.out.println(e.score);
        MarkupMsg save = netClass.serializeUpdates();
        e.score.clear();

        System.out.println(e.score);
        netClass.deserializeMsg(save);

        System.out.println(e.score);



    }

    /*private static void change(Entity e) {
        e.x = 33; e.y = 44; ((Wall)e).spriteId = "ball";
        if (e.entity != null) {
            e.entity.x = 1;
            e.entity.y = 2;
            ((Wall) e.entity).spriteId = "ball2";
        }
    }*/
}
