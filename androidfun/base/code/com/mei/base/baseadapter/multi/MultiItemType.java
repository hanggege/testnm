package com.mei.base.baseadapter.multi;

/**
 * Created by joker on 16/8/16.
 */
public enum MultiItemType {
    ItemType1(0),
    ItemType2(1),
    ItemType3(2),
    ItemType4(3),
    ItemType5(4),
    ItemType6(5),
    ItemType7(6),
    ItemType8(7),
    ItemType9(8),
    ItemType10(9),
    ItemType11(10),
    ItemType12(11),
    ItemType13(12),
    ItemType14(13),
    ItemType15(14);


    private int type;

    MultiItemType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean belongsTo(int state) {
        return this.type == state;
    }

    public static MultiItemType ofType(int type) {
        for (MultiItemType s : MultiItemType.values()) {
            if (s.belongsTo(type)) {
                return s;
            }
        }
        return MultiItemType.ItemType1;
    }


}
