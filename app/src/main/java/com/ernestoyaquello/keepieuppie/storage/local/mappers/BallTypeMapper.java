package com.ernestoyaquello.keepieuppie.storage.local.mappers;

import com.ernestoyaquello.keepieuppie.storage.local.models.BallType;

public class BallTypeMapper {

    public static int fromBallTypeToInteger(BallType instance) {
        switch (instance) {
            case Diamond:
                return 5;
            case Titanium:
                return 4;
            case Gold:
                return 3;
            case Silver:
                return 2;
            case Bronze:
                return 1;
            case Standard:
            default:
                return 0;
        }
    }

    public static BallType fromIntegerToBallType(int value) {
        switch (value) {
            case 5:
                return BallType.Diamond;
            case 4:
                return BallType.Titanium;
            case 3:
                return BallType.Gold;
            case 2:
                return BallType.Silver;
            case 1:
                return BallType.Bronze;
            case 0:
            default:
                return BallType.Standard;
        }
    }
}
