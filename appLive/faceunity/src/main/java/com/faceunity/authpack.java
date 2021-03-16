package com.faceunity;

import java.security.MessageDigest;

public class authpack {
    public static int sha1_32(byte[] buf) {
        int ret = 0;
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(buf);
            return ((int) (digest[0] & 0xff) << 24) + ((int) (digest[1] & 0xff) << 16) + ((int) (digest[2] & 0xff) << 8) + ((int) (digest[3] & 0xff) << 0);
        } catch (Exception e) {
        }
        return ret;
    }

    public static byte[] A() {
        byte[] buf = new byte[1122];
        int i = 0;
        for (i = 56; i < 84; i++) {
            buf[0] = (byte) i;
            if (sha1_32(buf) == -1555101928) {
                break;
            }
        }
        for (i = -22; i < 2; i++) {
            buf[1] = (byte) i;
            if (sha1_32(buf) == 2108405417) {
                break;
            }
        }
        for (i = 29; i < 35; i++) {
            buf[2] = (byte) i;
            if (sha1_32(buf) == -128118360) {
                break;
            }
        }
        for (i = -119; i < -107; i++) {
            buf[3] = (byte) i;
            if (sha1_32(buf) == -500681915) {
                break;
            }
        }
        for (i = -113; i < -106; i++) {
            buf[4] = (byte) i;
            if (sha1_32(buf) == -828137048) {
                break;
            }
        }
        for (i = 121; i < 128; i++) {
            buf[5] = (byte) i;
            if (sha1_32(buf) == -2029393567) {
                break;
            }
        }
        for (i = 9; i < 22; i++) {
            buf[6] = (byte) i;
            if (sha1_32(buf) == 1689398283) {
                break;
            }
        }
        for (i = 15; i < 37; i++) {
            buf[7] = (byte) i;
            if (sha1_32(buf) == 1268271976) {
                break;
            }
        }
        for (i = -83; i < -58; i++) {
            buf[8] = (byte) i;
            if (sha1_32(buf) == 800242085) {
                break;
            }
        }
        for (i = -20; i < -5; i++) {
            buf[9] = (byte) i;
            if (sha1_32(buf) == -2040942874) {
                break;
            }
        }
        for (i = 37; i < 39; i++) {
            buf[10] = (byte) i;
            if (sha1_32(buf) == 910241168) {
                break;
            }
        }
        for (i = -52; i < -34; i++) {
            buf[11] = (byte) i;
            if (sha1_32(buf) == -1881409568) {
                break;
            }
        }
        for (i = -99; i < -79; i++) {
            buf[12] = (byte) i;
            if (sha1_32(buf) == 774287926) {
                break;
            }
        }
        for (i = 61; i < 84; i++) {
            buf[13] = (byte) i;
            if (sha1_32(buf) == -50549018) {
                break;
            }
        }
        for (i = -122; i < -106; i++) {
            buf[14] = (byte) i;
            if (sha1_32(buf) == -30706867) {
                break;
            }
        }
        for (i = 59; i < 80; i++) {
            buf[15] = (byte) i;
            if (sha1_32(buf) == -2053050478) {
                break;
            }
        }
        for (i = -110; i < -101; i++) {
            buf[16] = (byte) i;
            if (sha1_32(buf) == 1812464903) {
                break;
            }
        }
        for (i = 9; i < 25; i++) {
            buf[17] = (byte) i;
            if (sha1_32(buf) == -393680041) {
                break;
            }
        }
        for (i = -1; i < 2; i++) {
            buf[18] = (byte) i;
            if (sha1_32(buf) == -1046955807) {
                break;
            }
        }
        for (i = -108; i < -88; i++) {
            buf[19] = (byte) i;
            if (sha1_32(buf) == 1746467624) {
                break;
            }
        }
        for (i = -123; i < -102; i++) {
            buf[20] = (byte) i;
            if (sha1_32(buf) == 281828156) {
                break;
            }
        }
        for (i = 9; i < 30; i++) {
            buf[21] = (byte) i;
            if (sha1_32(buf) == 1512749692) {
                break;
            }
        }
        for (i = 6; i < 15; i++) {
            buf[22] = (byte) i;
            if (sha1_32(buf) == 1733169953) {
                break;
            }
        }
        for (i = 13; i < 31; i++) {
            buf[23] = (byte) i;
            if (sha1_32(buf) == -1475835572) {
                break;
            }
        }
        for (i = -83; i < -57; i++) {
            buf[24] = (byte) i;
            if (sha1_32(buf) == 1684327803) {
                break;
            }
        }
        for (i = -78; i < -62; i++) {
            buf[25] = (byte) i;
            if (sha1_32(buf) == 660202641) {
                break;
            }
        }
        for (i = -69; i < -60; i++) {
            buf[26] = (byte) i;
            if (sha1_32(buf) == -2001843961) {
                break;
            }
        }
        for (i = -55; i < -30; i++) {
            buf[27] = (byte) i;
            if (sha1_32(buf) == 1824298423) {
                break;
            }
        }
        for (i = -21; i < 2; i++) {
            buf[28] = (byte) i;
            if (sha1_32(buf) == 1587070354) {
                break;
            }
        }
        for (i = -58; i < -31; i++) {
            buf[29] = (byte) i;
            if (sha1_32(buf) == 484275225) {
                break;
            }
        }
        for (i = 71; i < 80; i++) {
            buf[30] = (byte) i;
            if (sha1_32(buf) == -426484865) {
                break;
            }
        }
        for (i = 0; i < 4; i++) {
            buf[31] = (byte) i;
            if (sha1_32(buf) == -426484865) {
                break;
            }
        }
        for (i = 74; i < 96; i++) {
            buf[32] = (byte) i;
            if (sha1_32(buf) == 951305015) {
                break;
            }
        }
        for (i = 21; i < 38; i++) {
            buf[33] = (byte) i;
            if (sha1_32(buf) == 1902862574) {
                break;
            }
        }
        for (i = 14; i < 26; i++) {
            buf[34] = (byte) i;
            if (sha1_32(buf) == 799637461) {
                break;
            }
        }
        for (i = -86; i < -70; i++) {
            buf[35] = (byte) i;
            if (sha1_32(buf) == -839011746) {
                break;
            }
        }
        for (i = 104; i < 116; i++) {
            buf[36] = (byte) i;
            if (sha1_32(buf) == -576812041) {
                break;
            }
        }
        for (i = -28; i < -5; i++) {
            buf[37] = (byte) i;
            if (sha1_32(buf) == 1439960684) {
                break;
            }
        }
        for (i = 39; i < 45; i++) {
            buf[38] = (byte) i;
            if (sha1_32(buf) == -1010736584) {
                break;
            }
        }
        for (i = 69; i < 84; i++) {
            buf[39] = (byte) i;
            if (sha1_32(buf) == 746607600) {
                break;
            }
        }
        for (i = -83; i < -72; i++) {
            buf[40] = (byte) i;
            if (sha1_32(buf) == -1709939303) {
                break;
            }
        }
        for (i = -35; i < -19; i++) {
            buf[41] = (byte) i;
            if (sha1_32(buf) == -2134803585) {
                break;
            }
        }
        for (i = 74; i < 92; i++) {
            buf[42] = (byte) i;
            if (sha1_32(buf) == -232889799) {
                break;
            }
        }
        for (i = 77; i < 93; i++) {
            buf[43] = (byte) i;
            if (sha1_32(buf) == -1691677607) {
                break;
            }
        }
        for (i = -57; i < -51; i++) {
            buf[44] = (byte) i;
            if (sha1_32(buf) == -1439925498) {
                break;
            }
        }
        for (i = 1; i < 8; i++) {
            buf[45] = (byte) i;
            if (sha1_32(buf) == -73614296) {
                break;
            }
        }
        for (i = -104; i < -83; i++) {
            buf[46] = (byte) i;
            if (sha1_32(buf) == 1643225526) {
                break;
            }
        }
        for (i = 74; i < 96; i++) {
            buf[47] = (byte) i;
            if (sha1_32(buf) == 1778638406) {
                break;
            }
        }
        for (i = 86; i < 95; i++) {
            buf[48] = (byte) i;
            if (sha1_32(buf) == 1633768302) {
                break;
            }
        }
        for (i = -61; i < -53; i++) {
            buf[49] = (byte) i;
            if (sha1_32(buf) == -734805278) {
                break;
            }
        }
        for (i = -128; i < -110; i++) {
            buf[50] = (byte) i;
            if (sha1_32(buf) == -1688577142) {
                break;
            }
        }
        for (i = -84; i < -71; i++) {
            buf[51] = (byte) i;
            if (sha1_32(buf) == 1582419358) {
                break;
            }
        }
        for (i = -61; i < -40; i++) {
            buf[52] = (byte) i;
            if (sha1_32(buf) == 1366114012) {
                break;
            }
        }
        for (i = -128; i < -109; i++) {
            buf[53] = (byte) i;
            if (sha1_32(buf) == -1631677732) {
                break;
            }
        }
        for (i = -12; i < 0; i++) {
            buf[54] = (byte) i;
            if (sha1_32(buf) == 1303825811) {
                break;
            }
        }
        for (i = 32; i < 53; i++) {
            buf[55] = (byte) i;
            if (sha1_32(buf) == 446881337) {
                break;
            }
        }
        for (i = -26; i < -17; i++) {
            buf[56] = (byte) i;
            if (sha1_32(buf) == -1180264876) {
                break;
            }
        }
        for (i = 13; i < 27; i++) {
            buf[57] = (byte) i;
            if (sha1_32(buf) == -1738313406) {
                break;
            }
        }
        for (i = -28; i < -15; i++) {
            buf[58] = (byte) i;
            if (sha1_32(buf) == -1371086645) {
                break;
            }
        }
        for (i = -127; i < -107; i++) {
            buf[59] = (byte) i;
            if (sha1_32(buf) == 1498124021) {
                break;
            }
        }
        for (i = -77; i < -60; i++) {
            buf[60] = (byte) i;
            if (sha1_32(buf) == -2030862515) {
                break;
            }
        }
        for (i = 44; i < 68; i++) {
            buf[61] = (byte) i;
            if (sha1_32(buf) == -1322592970) {
                break;
            }
        }
        for (i = -8; i < 7; i++) {
            buf[62] = (byte) i;
            if (sha1_32(buf) == 388435851) {
                break;
            }
        }
        for (i = -62; i < -46; i++) {
            buf[63] = (byte) i;
            if (sha1_32(buf) == -220864791) {
                break;
            }
        }
        for (i = 21; i < 33; i++) {
            buf[64] = (byte) i;
            if (sha1_32(buf) == -1719267054) {
                break;
            }
        }
        for (i = 111; i < 128; i++) {
            buf[65] = (byte) i;
            if (sha1_32(buf) == 1003273487) {
                break;
            }
        }
        for (i = 105; i < 114; i++) {
            buf[66] = (byte) i;
            if (sha1_32(buf) == -1187684300) {
                break;
            }
        }
        for (i = -34; i < -11; i++) {
            buf[67] = (byte) i;
            if (sha1_32(buf) == 1903580162) {
                break;
            }
        }
        for (i = 78; i < 89; i++) {
            buf[68] = (byte) i;
            if (sha1_32(buf) == -292876713) {
                break;
            }
        }
        for (i = -128; i < -112; i++) {
            buf[69] = (byte) i;
            if (sha1_32(buf) == -703423649) {
                break;
            }
        }
        for (i = -96; i < -85; i++) {
            buf[70] = (byte) i;
            if (sha1_32(buf) == 897129257) {
                break;
            }
        }
        for (i = -49; i < -22; i++) {
            buf[71] = (byte) i;
            if (sha1_32(buf) == 1961807741) {
                break;
            }
        }
        for (i = -117; i < -111; i++) {
            buf[72] = (byte) i;
            if (sha1_32(buf) == 1194207249) {
                break;
            }
        }
        for (i = -6; i < 5; i++) {
            buf[73] = (byte) i;
            if (sha1_32(buf) == -1619225764) {
                break;
            }
        }
        for (i = 75; i < 86; i++) {
            buf[74] = (byte) i;
            if (sha1_32(buf) == -290912478) {
                break;
            }
        }
        for (i = -119; i < -97; i++) {
            buf[75] = (byte) i;
            if (sha1_32(buf) == -1640163634) {
                break;
            }
        }
        for (i = -8; i < 13; i++) {
            buf[76] = (byte) i;
            if (sha1_32(buf) == -52343446) {
                break;
            }
        }
        for (i = -18; i < 6; i++) {
            buf[77] = (byte) i;
            if (sha1_32(buf) == -330357381) {
                break;
            }
        }
        for (i = 71; i < 79; i++) {
            buf[78] = (byte) i;
            if (sha1_32(buf) == 1758324581) {
                break;
            }
        }
        for (i = -117; i < -91; i++) {
            buf[79] = (byte) i;
            if (sha1_32(buf) == -373975041) {
                break;
            }
        }
        for (i = -81; i < -66; i++) {
            buf[80] = (byte) i;
            if (sha1_32(buf) == 1829240310) {
                break;
            }
        }
        for (i = -41; i < -34; i++) {
            buf[81] = (byte) i;
            if (sha1_32(buf) == -1076342420) {
                break;
            }
        }
        for (i = 111; i < 118; i++) {
            buf[82] = (byte) i;
            if (sha1_32(buf) == -167548473) {
                break;
            }
        }
        for (i = -109; i < -95; i++) {
            buf[83] = (byte) i;
            if (sha1_32(buf) == 367061312) {
                break;
            }
        }
        for (i = 12; i < 29; i++) {
            buf[84] = (byte) i;
            if (sha1_32(buf) == 404986646) {
                break;
            }
        }
        for (i = 86; i < 107; i++) {
            buf[85] = (byte) i;
            if (sha1_32(buf) == 176784715) {
                break;
            }
        }
        for (i = 30; i < 52; i++) {
            buf[86] = (byte) i;
            if (sha1_32(buf) == 1946852598) {
                break;
            }
        }
        for (i = -84; i < -66; i++) {
            buf[87] = (byte) i;
            if (sha1_32(buf) == -1640856461) {
                break;
            }
        }
        for (i = 39; i < 53; i++) {
            buf[88] = (byte) i;
            if (sha1_32(buf) == -1382018990) {
                break;
            }
        }
        for (i = 106; i < 119; i++) {
            buf[89] = (byte) i;
            if (sha1_32(buf) == 295614886) {
                break;
            }
        }
        for (i = -72; i < -61; i++) {
            buf[90] = (byte) i;
            if (sha1_32(buf) == -2068826932) {
                break;
            }
        }
        for (i = -13; i < -7; i++) {
            buf[91] = (byte) i;
            if (sha1_32(buf) == -1605243061) {
                break;
            }
        }
        for (i = -11; i < 9; i++) {
            buf[92] = (byte) i;
            if (sha1_32(buf) == 374034346) {
                break;
            }
        }
        for (i = 46; i < 71; i++) {
            buf[93] = (byte) i;
            if (sha1_32(buf) == -869815636) {
                break;
            }
        }
        for (i = 22; i < 41; i++) {
            buf[94] = (byte) i;
            if (sha1_32(buf) == -1547613866) {
                break;
            }
        }
        for (i = -122; i < -102; i++) {
            buf[95] = (byte) i;
            if (sha1_32(buf) == 1851076287) {
                break;
            }
        }
        for (i = 90; i < 105; i++) {
            buf[96] = (byte) i;
            if (sha1_32(buf) == -649535879) {
                break;
            }
        }
        for (i = 94; i < 113; i++) {
            buf[97] = (byte) i;
            if (sha1_32(buf) == 1738075221) {
                break;
            }
        }
        for (i = -50; i < -31; i++) {
            buf[98] = (byte) i;
            if (sha1_32(buf) == 375905410) {
                break;
            }
        }
        for (i = -26; i < -7; i++) {
            buf[99] = (byte) i;
            if (sha1_32(buf) == -777292731) {
                break;
            }
        }
        for (i = -88; i < -66; i++) {
            buf[100] = (byte) i;
            if (sha1_32(buf) == 273755374) {
                break;
            }
        }
        for (i = -45; i < -23; i++) {
            buf[101] = (byte) i;
            if (sha1_32(buf) == 447942385) {
                break;
            }
        }
        for (i = 65; i < 87; i++) {
            buf[102] = (byte) i;
            if (sha1_32(buf) == -2101096787) {
                break;
            }
        }
        for (i = 83; i < 103; i++) {
            buf[103] = (byte) i;
            if (sha1_32(buf) == 1198492579) {
                break;
            }
        }
        for (i = -51; i < -33; i++) {
            buf[104] = (byte) i;
            if (sha1_32(buf) == 641960569) {
                break;
            }
        }
        for (i = 76; i < 85; i++) {
            buf[105] = (byte) i;
            if (sha1_32(buf) == 321521908) {
                break;
            }
        }
        for (i = -112; i < -96; i++) {
            buf[106] = (byte) i;
            if (sha1_32(buf) == -2015798417) {
                break;
            }
        }
        for (i = -33; i < -29; i++) {
            buf[107] = (byte) i;
            if (sha1_32(buf) == -1408640800) {
                break;
            }
        }
        for (i = -125; i < -107; i++) {
            buf[108] = (byte) i;
            if (sha1_32(buf) == -772205129) {
                break;
            }
        }
        for (i = -56; i < -32; i++) {
            buf[109] = (byte) i;
            if (sha1_32(buf) == -1477581385) {
                break;
            }
        }
        for (i = -128; i < -106; i++) {
            buf[110] = (byte) i;
            if (sha1_32(buf) == 1665367934) {
                break;
            }
        }
        for (i = -83; i < -68; i++) {
            buf[111] = (byte) i;
            if (sha1_32(buf) == -156974569) {
                break;
            }
        }
        for (i = -15; i < 1; i++) {
            buf[112] = (byte) i;
            if (sha1_32(buf) == 1331516398) {
                break;
            }
        }
        for (i = -2; i < 25; i++) {
            buf[113] = (byte) i;
            if (sha1_32(buf) == -199559447) {
                break;
            }
        }
        for (i = 18; i < 43; i++) {
            buf[114] = (byte) i;
            if (sha1_32(buf) == -2039998548) {
                break;
            }
        }
        for (i = -109; i < -96; i++) {
            buf[115] = (byte) i;
            if (sha1_32(buf) == -637291605) {
                break;
            }
        }
        for (i = -9; i < 11; i++) {
            buf[116] = (byte) i;
            if (sha1_32(buf) == -637291605) {
                break;
            }
        }
        for (i = 35; i < 49; i++) {
            buf[117] = (byte) i;
            if (sha1_32(buf) == 68953729) {
                break;
            }
        }
        for (i = 70; i < 88; i++) {
            buf[118] = (byte) i;
            if (sha1_32(buf) == 631119516) {
                break;
            }
        }
        for (i = -125; i < -103; i++) {
            buf[119] = (byte) i;
            if (sha1_32(buf) == -1724728760) {
                break;
            }
        }
        for (i = -69; i < -52; i++) {
            buf[120] = (byte) i;
            if (sha1_32(buf) == 599981018) {
                break;
            }
        }
        for (i = 11; i < 24; i++) {
            buf[121] = (byte) i;
            if (sha1_32(buf) == 649695362) {
                break;
            }
        }
        for (i = -12; i < 12; i++) {
            buf[122] = (byte) i;
            if (sha1_32(buf) == 389671353) {
                break;
            }
        }
        for (i = 52; i < 67; i++) {
            buf[123] = (byte) i;
            if (sha1_32(buf) == -1334094731) {
                break;
            }
        }
        for (i = -63; i < -54; i++) {
            buf[124] = (byte) i;
            if (sha1_32(buf) == -1750163032) {
                break;
            }
        }
        for (i = 39; i < 60; i++) {
            buf[125] = (byte) i;
            if (sha1_32(buf) == -819196425) {
                break;
            }
        }
        for (i = -55; i < -41; i++) {
            buf[126] = (byte) i;
            if (sha1_32(buf) == -962525654) {
                break;
            }
        }
        for (i = 23; i < 43; i++) {
            buf[127] = (byte) i;
            if (sha1_32(buf) == -1220014963) {
                break;
            }
        }
        for (i = -40; i < -31; i++) {
            buf[128] = (byte) i;
            if (sha1_32(buf) == -912802631) {
                break;
            }
        }
        for (i = 24; i < 34; i++) {
            buf[129] = (byte) i;
            if (sha1_32(buf) == 194887244) {
                break;
            }
        }
        for (i = -4; i < 9; i++) {
            buf[130] = (byte) i;
            if (sha1_32(buf) == -526573489) {
                break;
            }
        }
        for (i = 121; i < 128; i++) {
            buf[131] = (byte) i;
            if (sha1_32(buf) == -833297642) {
                break;
            }
        }
        for (i = 61; i < 77; i++) {
            buf[132] = (byte) i;
            if (sha1_32(buf) == 1897722245) {
                break;
            }
        }
        for (i = -3; i < 22; i++) {
            buf[133] = (byte) i;
            if (sha1_32(buf) == 2112205250) {
                break;
            }
        }
        for (i = -85; i < -69; i++) {
            buf[134] = (byte) i;
            if (sha1_32(buf) == 258949357) {
                break;
            }
        }
        for (i = -89; i < -80; i++) {
            buf[135] = (byte) i;
            if (sha1_32(buf) == -1425712808) {
                break;
            }
        }
        for (i = -123; i < -115; i++) {
            buf[136] = (byte) i;
            if (sha1_32(buf) == 737796581) {
                break;
            }
        }
        for (i = 13; i < 21; i++) {
            buf[137] = (byte) i;
            if (sha1_32(buf) == -439232303) {
                break;
            }
        }
        for (i = 110; i < 125; i++) {
            buf[138] = (byte) i;
            if (sha1_32(buf) == 882912989) {
                break;
            }
        }
        for (i = 53; i < 66; i++) {
            buf[139] = (byte) i;
            if (sha1_32(buf) == 792246472) {
                break;
            }
        }
        for (i = 37; i < 42; i++) {
            buf[140] = (byte) i;
            if (sha1_32(buf) == 656160575) {
                break;
            }
        }
        for (i = -85; i < -73; i++) {
            buf[141] = (byte) i;
            if (sha1_32(buf) == -138357391) {
                break;
            }
        }
        for (i = -15; i < -2; i++) {
            buf[142] = (byte) i;
            if (sha1_32(buf) == -241479725) {
                break;
            }
        }
        for (i = -4; i < 16; i++) {
            buf[143] = (byte) i;
            if (sha1_32(buf) == -559406819) {
                break;
            }
        }
        for (i = 37; i < 40; i++) {
            buf[144] = (byte) i;
            if (sha1_32(buf) == -739849048) {
                break;
            }
        }
        for (i = -32; i < -12; i++) {
            buf[145] = (byte) i;
            if (sha1_32(buf) == 1332250695) {
                break;
            }
        }
        for (i = 73; i < 89; i++) {
            buf[146] = (byte) i;
            if (sha1_32(buf) == -60761738) {
                break;
            }
        }
        for (i = 89; i < 105; i++) {
            buf[147] = (byte) i;
            if (sha1_32(buf) == 377251564) {
                break;
            }
        }
        for (i = 35; i < 47; i++) {
            buf[148] = (byte) i;
            if (sha1_32(buf) == -1698578181) {
                break;
            }
        }
        for (i = -128; i < -105; i++) {
            buf[149] = (byte) i;
            if (sha1_32(buf) == 36279891) {
                break;
            }
        }
        for (i = 4; i < 23; i++) {
            buf[150] = (byte) i;
            if (sha1_32(buf) == 1473101370) {
                break;
            }
        }
        for (i = 83; i < 107; i++) {
            buf[151] = (byte) i;
            if (sha1_32(buf) == -162478262) {
                break;
            }
        }
        for (i = -14; i < 15; i++) {
            buf[152] = (byte) i;
            if (sha1_32(buf) == -1250540046) {
                break;
            }
        }
        for (i = -100; i < -84; i++) {
            buf[153] = (byte) i;
            if (sha1_32(buf) == -1153901437) {
                break;
            }
        }
        for (i = -80; i < -60; i++) {
            buf[154] = (byte) i;
            if (sha1_32(buf) == -1887916656) {
                break;
            }
        }
        for (i = -84; i < -72; i++) {
            buf[155] = (byte) i;
            if (sha1_32(buf) == 740091984) {
                break;
            }
        }
        for (i = -50; i < -41; i++) {
            buf[156] = (byte) i;
            if (sha1_32(buf) == -1485871975) {
                break;
            }
        }
        for (i = 48; i < 67; i++) {
            buf[157] = (byte) i;
            if (sha1_32(buf) == -1351441389) {
                break;
            }
        }
        for (i = 23; i < 34; i++) {
            buf[158] = (byte) i;
            if (sha1_32(buf) == -2092829543) {
                break;
            }
        }
        for (i = 39; i < 64; i++) {
            buf[159] = (byte) i;
            if (sha1_32(buf) == 736515065) {
                break;
            }
        }
        for (i = -9; i < 8; i++) {
            buf[160] = (byte) i;
            if (sha1_32(buf) == -259381766) {
                break;
            }
        }
        for (i = -11; i < 4; i++) {
            buf[161] = (byte) i;
            if (sha1_32(buf) == -259381766) {
                break;
            }
        }
        for (i = 36; i < 49; i++) {
            buf[162] = (byte) i;
            if (sha1_32(buf) == -1866914379) {
                break;
            }
        }
        for (i = 91; i < 108; i++) {
            buf[163] = (byte) i;
            if (sha1_32(buf) == -1458193608) {
                break;
            }
        }
        for (i = -13; i < 2; i++) {
            buf[164] = (byte) i;
            if (sha1_32(buf) == 1717591253) {
                break;
            }
        }
        for (i = -38; i < -24; i++) {
            buf[165] = (byte) i;
            if (sha1_32(buf) == -1816991521) {
                break;
            }
        }
        for (i = 3; i < 14; i++) {
            buf[166] = (byte) i;
            if (sha1_32(buf) == -1254608609) {
                break;
            }
        }
        for (i = 43; i < 64; i++) {
            buf[167] = (byte) i;
            if (sha1_32(buf) == -1343828315) {
                break;
            }
        }
        for (i = -3; i < 24; i++) {
            buf[168] = (byte) i;
            if (sha1_32(buf) == -1540365520) {
                break;
            }
        }
        for (i = -71; i < -56; i++) {
            buf[169] = (byte) i;
            if (sha1_32(buf) == 982862371) {
                break;
            }
        }
        for (i = -77; i < -67; i++) {
            buf[170] = (byte) i;
            if (sha1_32(buf) == -2140150513) {
                break;
            }
        }
        for (i = -31; i < -2; i++) {
            buf[171] = (byte) i;
            if (sha1_32(buf) == -1905861493) {
                break;
            }
        }
        for (i = 58; i < 71; i++) {
            buf[172] = (byte) i;
            if (sha1_32(buf) == 1093031295) {
                break;
            }
        }
        for (i = -76; i < -59; i++) {
            buf[173] = (byte) i;
            if (sha1_32(buf) == -1522892101) {
                break;
            }
        }
        for (i = 46; i < 62; i++) {
            buf[174] = (byte) i;
            if (sha1_32(buf) == 104384862) {
                break;
            }
        }
        for (i = 24; i < 39; i++) {
            buf[175] = (byte) i;
            if (sha1_32(buf) == -573841971) {
                break;
            }
        }
        for (i = -50; i < -27; i++) {
            buf[176] = (byte) i;
            if (sha1_32(buf) == -770260551) {
                break;
            }
        }
        for (i = -81; i < -64; i++) {
            buf[177] = (byte) i;
            if (sha1_32(buf) == -917611207) {
                break;
            }
        }
        for (i = -98; i < -80; i++) {
            buf[178] = (byte) i;
            if (sha1_32(buf) == 1155410695) {
                break;
            }
        }
        for (i = -68; i < -40; i++) {
            buf[179] = (byte) i;
            if (sha1_32(buf) == 668099642) {
                break;
            }
        }
        for (i = 20; i < 37; i++) {
            buf[180] = (byte) i;
            if (sha1_32(buf) == -934105097) {
                break;
            }
        }
        for (i = -112; i < -93; i++) {
            buf[181] = (byte) i;
            if (sha1_32(buf) == -862515828) {
                break;
            }
        }
        for (i = -1; i < 18; i++) {
            buf[182] = (byte) i;
            if (sha1_32(buf) == 1774924345) {
                break;
            }
        }
        for (i = 51; i < 73; i++) {
            buf[183] = (byte) i;
            if (sha1_32(buf) == 659633853) {
                break;
            }
        }
        for (i = 28; i < 36; i++) {
            buf[184] = (byte) i;
            if (sha1_32(buf) == 2033721785) {
                break;
            }
        }
        for (i = -45; i < -27; i++) {
            buf[185] = (byte) i;
            if (sha1_32(buf) == 631966506) {
                break;
            }
        }
        for (i = -58; i < -51; i++) {
            buf[186] = (byte) i;
            if (sha1_32(buf) == -42729515) {
                break;
            }
        }
        for (i = -13; i < 3; i++) {
            buf[187] = (byte) i;
            if (sha1_32(buf) == 2138131769) {
                break;
            }
        }
        for (i = -54; i < -45; i++) {
            buf[188] = (byte) i;
            if (sha1_32(buf) == 1457136202) {
                break;
            }
        }
        for (i = -27; i < -4; i++) {
            buf[189] = (byte) i;
            if (sha1_32(buf) == -1902394552) {
                break;
            }
        }
        for (i = -11; i < 2; i++) {
            buf[190] = (byte) i;
            if (sha1_32(buf) == 1936058725) {
                break;
            }
        }
        for (i = 96; i < 121; i++) {
            buf[191] = (byte) i;
            if (sha1_32(buf) == -2051182050) {
                break;
            }
        }
        for (i = 45; i < 69; i++) {
            buf[192] = (byte) i;
            if (sha1_32(buf) == 1199298059) {
                break;
            }
        }
        for (i = 101; i < 114; i++) {
            buf[193] = (byte) i;
            if (sha1_32(buf) == -633285454) {
                break;
            }
        }
        for (i = -89; i < -84; i++) {
            buf[194] = (byte) i;
            if (sha1_32(buf) == -1960485433) {
                break;
            }
        }
        for (i = 99; i < 120; i++) {
            buf[195] = (byte) i;
            if (sha1_32(buf) == 1950989251) {
                break;
            }
        }
        for (i = 43; i < 53; i++) {
            buf[196] = (byte) i;
            if (sha1_32(buf) == 2123441081) {
                break;
            }
        }
        for (i = 81; i < 94; i++) {
            buf[197] = (byte) i;
            if (sha1_32(buf) == 2054236530) {
                break;
            }
        }
        for (i = 19; i < 29; i++) {
            buf[198] = (byte) i;
            if (sha1_32(buf) == -768074444) {
                break;
            }
        }
        for (i = 66; i < 81; i++) {
            buf[199] = (byte) i;
            if (sha1_32(buf) == 360050705) {
                break;
            }
        }
        for (i = 68; i < 83; i++) {
            buf[200] = (byte) i;
            if (sha1_32(buf) == 382149454) {
                break;
            }
        }
        for (i = -111; i < -90; i++) {
            buf[201] = (byte) i;
            if (sha1_32(buf) == -2140256922) {
                break;
            }
        }
        for (i = 2; i < 12; i++) {
            buf[202] = (byte) i;
            if (sha1_32(buf) == 1612237447) {
                break;
            }
        }
        for (i = -79; i < -73; i++) {
            buf[203] = (byte) i;
            if (sha1_32(buf) == 1709261826) {
                break;
            }
        }
        for (i = 92; i < 103; i++) {
            buf[204] = (byte) i;
            if (sha1_32(buf) == -232101006) {
                break;
            }
        }
        for (i = -80; i < -62; i++) {
            buf[205] = (byte) i;
            if (sha1_32(buf) == 587365726) {
                break;
            }
        }
        for (i = 49; i < 67; i++) {
            buf[206] = (byte) i;
            if (sha1_32(buf) == -549911192) {
                break;
            }
        }
        for (i = -40; i < -14; i++) {
            buf[207] = (byte) i;
            if (sha1_32(buf) == -1546837754) {
                break;
            }
        }
        for (i = -31; i < -16; i++) {
            buf[208] = (byte) i;
            if (sha1_32(buf) == -1190686707) {
                break;
            }
        }
        for (i = -128; i < -107; i++) {
            buf[209] = (byte) i;
            if (sha1_32(buf) == -903050702) {
                break;
            }
        }
        for (i = 69; i < 78; i++) {
            buf[210] = (byte) i;
            if (sha1_32(buf) == 678107745) {
                break;
            }
        }
        for (i = -56; i < -45; i++) {
            buf[211] = (byte) i;
            if (sha1_32(buf) == -1276420671) {
                break;
            }
        }
        for (i = -77; i < -64; i++) {
            buf[212] = (byte) i;
            if (sha1_32(buf) == 2031978108) {
                break;
            }
        }
        for (i = -57; i < -33; i++) {
            buf[213] = (byte) i;
            if (sha1_32(buf) == 1949996210) {
                break;
            }
        }
        for (i = -128; i < -116; i++) {
            buf[214] = (byte) i;
            if (sha1_32(buf) == -1892753772) {
                break;
            }
        }
        for (i = 16; i < 40; i++) {
            buf[215] = (byte) i;
            if (sha1_32(buf) == 1029819116) {
                break;
            }
        }
        for (i = 27; i < 47; i++) {
            buf[216] = (byte) i;
            if (sha1_32(buf) == -1432274664) {
                break;
            }
        }
        for (i = -59; i < -38; i++) {
            buf[217] = (byte) i;
            if (sha1_32(buf) == -1913499546) {
                break;
            }
        }
        for (i = -44; i < -41; i++) {
            buf[218] = (byte) i;
            if (sha1_32(buf) == -205579025) {
                break;
            }
        }
        for (i = 33; i < 52; i++) {
            buf[219] = (byte) i;
            if (sha1_32(buf) == -31978967) {
                break;
            }
        }
        for (i = 21; i < 35; i++) {
            buf[220] = (byte) i;
            if (sha1_32(buf) == -531409156) {
                break;
            }
        }
        for (i = -10; i < 7; i++) {
            buf[221] = (byte) i;
            if (sha1_32(buf) == -1106175754) {
                break;
            }
        }
        for (i = 71; i < 80; i++) {
            buf[222] = (byte) i;
            if (sha1_32(buf) == 105863276) {
                break;
            }
        }
        for (i = -52; i < -34; i++) {
            buf[223] = (byte) i;
            if (sha1_32(buf) == 178318390) {
                break;
            }
        }
        for (i = 40; i < 58; i++) {
            buf[224] = (byte) i;
            if (sha1_32(buf) == 1211329111) {
                break;
            }
        }
        for (i = 92; i < 115; i++) {
            buf[225] = (byte) i;
            if (sha1_32(buf) == -1200012780) {
                break;
            }
        }
        for (i = -128; i < -116; i++) {
            buf[226] = (byte) i;
            if (sha1_32(buf) == -76319055) {
                break;
            }
        }
        for (i = 66; i < 85; i++) {
            buf[227] = (byte) i;
            if (sha1_32(buf) == 532755546) {
                break;
            }
        }
        for (i = 21; i < 36; i++) {
            buf[228] = (byte) i;
            if (sha1_32(buf) == 379421059) {
                break;
            }
        }
        for (i = 1; i < 18; i++) {
            buf[229] = (byte) i;
            if (sha1_32(buf) == 1366633959) {
                break;
            }
        }
        for (i = -1; i < 14; i++) {
            buf[230] = (byte) i;
            if (sha1_32(buf) == -827916086) {
                break;
            }
        }
        for (i = -12; i < 7; i++) {
            buf[231] = (byte) i;
            if (sha1_32(buf) == 425068006) {
                break;
            }
        }
        for (i = 56; i < 61; i++) {
            buf[232] = (byte) i;
            if (sha1_32(buf) == -1372466563) {
                break;
            }
        }
        for (i = 78; i < 94; i++) {
            buf[233] = (byte) i;
            if (sha1_32(buf) == 1100604198) {
                break;
            }
        }
        for (i = 32; i < 51; i++) {
            buf[234] = (byte) i;
            if (sha1_32(buf) == 1593628150) {
                break;
            }
        }
        for (i = 43; i < 46; i++) {
            buf[235] = (byte) i;
            if (sha1_32(buf) == 421296608) {
                break;
            }
        }
        for (i = -127; i < -109; i++) {
            buf[236] = (byte) i;
            if (sha1_32(buf) == -1049208667) {
                break;
            }
        }
        for (i = 114; i < 128; i++) {
            buf[237] = (byte) i;
            if (sha1_32(buf) == 811477530) {
                break;
            }
        }
        for (i = -79; i < -64; i++) {
            buf[238] = (byte) i;
            if (sha1_32(buf) == 753329384) {
                break;
            }
        }
        for (i = 34; i < 40; i++) {
            buf[239] = (byte) i;
            if (sha1_32(buf) == -1377592620) {
                break;
            }
        }
        for (i = -128; i < -110; i++) {
            buf[240] = (byte) i;
            if (sha1_32(buf) == -1982880529) {
                break;
            }
        }
        for (i = 71; i < 85; i++) {
            buf[241] = (byte) i;
            if (sha1_32(buf) == 1000839989) {
                break;
            }
        }
        for (i = 53; i < 62; i++) {
            buf[242] = (byte) i;
            if (sha1_32(buf) == -1432295466) {
                break;
            }
        }
        for (i = -128; i < -119; i++) {
            buf[243] = (byte) i;
            if (sha1_32(buf) == -2115340283) {
                break;
            }
        }
        for (i = -123; i < -103; i++) {
            buf[244] = (byte) i;
            if (sha1_32(buf) == 269786639) {
                break;
            }
        }
        for (i = -41; i < -25; i++) {
            buf[245] = (byte) i;
            if (sha1_32(buf) == 1162269904) {
                break;
            }
        }
        for (i = 65; i < 74; i++) {
            buf[246] = (byte) i;
            if (sha1_32(buf) == 1760773560) {
                break;
            }
        }
        for (i = 116; i < 128; i++) {
            buf[247] = (byte) i;
            if (sha1_32(buf) == -732850261) {
                break;
            }
        }
        for (i = -74; i < -51; i++) {
            buf[248] = (byte) i;
            if (sha1_32(buf) == -1062338592) {
                break;
            }
        }
        for (i = -9; i < 8; i++) {
            buf[249] = (byte) i;
            if (sha1_32(buf) == 1136388607) {
                break;
            }
        }
        for (i = 64; i < 81; i++) {
            buf[250] = (byte) i;
            if (sha1_32(buf) == 609780483) {
                break;
            }
        }
        for (i = -45; i < -22; i++) {
            buf[251] = (byte) i;
            if (sha1_32(buf) == -1360283483) {
                break;
            }
        }
        for (i = -71; i < -59; i++) {
            buf[252] = (byte) i;
            if (sha1_32(buf) == -1520791879) {
                break;
            }
        }
        for (i = -89; i < -68; i++) {
            buf[253] = (byte) i;
            if (sha1_32(buf) == -1708443739) {
                break;
            }
        }
        for (i = 77; i < 92; i++) {
            buf[254] = (byte) i;
            if (sha1_32(buf) == 1589273988) {
                break;
            }
        }
        for (i = 114; i < 128; i++) {
            buf[255] = (byte) i;
            if (sha1_32(buf) == -1011579434) {
                break;
            }
        }
        for (i = -89; i < -78; i++) {
            buf[256] = (byte) i;
            if (sha1_32(buf) == 1526587526) {
                break;
            }
        }
        for (i = 2; i < 27; i++) {
            buf[257] = (byte) i;
            if (sha1_32(buf) == 1008928359) {
                break;
            }
        }
        for (i = 75; i < 91; i++) {
            buf[258] = (byte) i;
            if (sha1_32(buf) == 1161858927) {
                break;
            }
        }
        for (i = 104; i < 106; i++) {
            buf[259] = (byte) i;
            if (sha1_32(buf) == 621236911) {
                break;
            }
        }
        for (i = -72; i < -51; i++) {
            buf[260] = (byte) i;
            if (sha1_32(buf) == 528534346) {
                break;
            }
        }
        for (i = -75; i < -60; i++) {
            buf[261] = (byte) i;
            if (sha1_32(buf) == -31840940) {
                break;
            }
        }
        for (i = 39; i < 65; i++) {
            buf[262] = (byte) i;
            if (sha1_32(buf) == 69961666) {
                break;
            }
        }
        for (i = 117; i < 128; i++) {
            buf[263] = (byte) i;
            if (sha1_32(buf) == 746723050) {
                break;
            }
        }
        for (i = -69; i < -45; i++) {
            buf[264] = (byte) i;
            if (sha1_32(buf) == -618387019) {
                break;
            }
        }
        for (i = -5; i < 1; i++) {
            buf[265] = (byte) i;
            if (sha1_32(buf) == -1062621065) {
                break;
            }
        }
        for (i = 33; i < 56; i++) {
            buf[266] = (byte) i;
            if (sha1_32(buf) == -865865913) {
                break;
            }
        }
        for (i = 84; i < 98; i++) {
            buf[267] = (byte) i;
            if (sha1_32(buf) == 528396696) {
                break;
            }
        }
        for (i = -114; i < -97; i++) {
            buf[268] = (byte) i;
            if (sha1_32(buf) == -982524559) {
                break;
            }
        }
        for (i = 69; i < 77; i++) {
            buf[269] = (byte) i;
            if (sha1_32(buf) == -1144349480) {
                break;
            }
        }
        for (i = -128; i < -124; i++) {
            buf[270] = (byte) i;
            if (sha1_32(buf) == 546383371) {
                break;
            }
        }
        for (i = -19; i < 3; i++) {
            buf[271] = (byte) i;
            if (sha1_32(buf) == 1505778997) {
                break;
            }
        }
        for (i = -110; i < -102; i++) {
            buf[272] = (byte) i;
            if (sha1_32(buf) == 1985275610) {
                break;
            }
        }
        for (i = 37; i < 49; i++) {
            buf[273] = (byte) i;
            if (sha1_32(buf) == -274245606) {
                break;
            }
        }
        for (i = 7; i < 27; i++) {
            buf[274] = (byte) i;
            if (sha1_32(buf) == 1963313444) {
                break;
            }
        }
        for (i = -23; i < -5; i++) {
            buf[275] = (byte) i;
            if (sha1_32(buf) == 53068950) {
                break;
            }
        }
        for (i = 74; i < 94; i++) {
            buf[276] = (byte) i;
            if (sha1_32(buf) == -814166827) {
                break;
            }
        }
        for (i = -53; i < -33; i++) {
            buf[277] = (byte) i;
            if (sha1_32(buf) == -1160459278) {
                break;
            }
        }
        for (i = -116; i < -97; i++) {
            buf[278] = (byte) i;
            if (sha1_32(buf) == -1887696583) {
                break;
            }
        }
        for (i = 110; i < 127; i++) {
            buf[279] = (byte) i;
            if (sha1_32(buf) == -1380997547) {
                break;
            }
        }
        for (i = -92; i < -74; i++) {
            buf[280] = (byte) i;
            if (sha1_32(buf) == -66444917) {
                break;
            }
        }
        for (i = 47; i < 72; i++) {
            buf[281] = (byte) i;
            if (sha1_32(buf) == 1639689022) {
                break;
            }
        }
        for (i = 38; i < 46; i++) {
            buf[282] = (byte) i;
            if (sha1_32(buf) == -1969302011) {
                break;
            }
        }
        for (i = 57; i < 72; i++) {
            buf[283] = (byte) i;
            if (sha1_32(buf) == 1220328089) {
                break;
            }
        }
        for (i = -46; i < -32; i++) {
            buf[284] = (byte) i;
            if (sha1_32(buf) == 1149616559) {
                break;
            }
        }
        for (i = 55; i < 70; i++) {
            buf[285] = (byte) i;
            if (sha1_32(buf) == -1887533578) {
                break;
            }
        }
        for (i = -13; i < 3; i++) {
            buf[286] = (byte) i;
            if (sha1_32(buf) == 1753162127) {
                break;
            }
        }
        for (i = 112; i < 124; i++) {
            buf[287] = (byte) i;
            if (sha1_32(buf) == 1934615693) {
                break;
            }
        }
        for (i = -17; i < -1; i++) {
            buf[288] = (byte) i;
            if (sha1_32(buf) == 621805904) {
                break;
            }
        }
        for (i = -2; i < 26; i++) {
            buf[289] = (byte) i;
            if (sha1_32(buf) == 128117107) {
                break;
            }
        }
        for (i = 109; i < 110; i++) {
            buf[290] = (byte) i;
            if (sha1_32(buf) == 1623754156) {
                break;
            }
        }
        for (i = -84; i < -66; i++) {
            buf[291] = (byte) i;
            if (sha1_32(buf) == 2048816449) {
                break;
            }
        }
        for (i = -86; i < -72; i++) {
            buf[292] = (byte) i;
            if (sha1_32(buf) == 1151551420) {
                break;
            }
        }
        for (i = 110; i < 128; i++) {
            buf[293] = (byte) i;
            if (sha1_32(buf) == -1836417134) {
                break;
            }
        }
        for (i = 44; i < 48; i++) {
            buf[294] = (byte) i;
            if (sha1_32(buf) == 1461049286) {
                break;
            }
        }
        for (i = 39; i < 55; i++) {
            buf[295] = (byte) i;
            if (sha1_32(buf) == -1933959380) {
                break;
            }
        }
        for (i = 48; i < 70; i++) {
            buf[296] = (byte) i;
            if (sha1_32(buf) == -701132577) {
                break;
            }
        }
        for (i = -1; i < 12; i++) {
            buf[297] = (byte) i;
            if (sha1_32(buf) == 406206415) {
                break;
            }
        }
        for (i = 54; i < 82; i++) {
            buf[298] = (byte) i;
            if (sha1_32(buf) == 96966100) {
                break;
            }
        }
        for (i = -121; i < -107; i++) {
            buf[299] = (byte) i;
            if (sha1_32(buf) == 375609275) {
                break;
            }
        }
        for (i = 103; i < 109; i++) {
            buf[300] = (byte) i;
            if (sha1_32(buf) == 1599982147) {
                break;
            }
        }
        for (i = -91; i < -72; i++) {
            buf[301] = (byte) i;
            if (sha1_32(buf) == 104360830) {
                break;
            }
        }
        for (i = 62; i < 73; i++) {
            buf[302] = (byte) i;
            if (sha1_32(buf) == 1446126778) {
                break;
            }
        }
        for (i = 38; i < 53; i++) {
            buf[303] = (byte) i;
            if (sha1_32(buf) == 1370255868) {
                break;
            }
        }
        for (i = 97; i < 123; i++) {
            buf[304] = (byte) i;
            if (sha1_32(buf) == -783201492) {
                break;
            }
        }
        for (i = 39; i < 54; i++) {
            buf[305] = (byte) i;
            if (sha1_32(buf) == -606776409) {
                break;
            }
        }
        for (i = -80; i < -68; i++) {
            buf[306] = (byte) i;
            if (sha1_32(buf) == -230865902) {
                break;
            }
        }
        for (i = 2; i < 28; i++) {
            buf[307] = (byte) i;
            if (sha1_32(buf) == -580866459) {
                break;
            }
        }
        for (i = 97; i < 100; i++) {
            buf[308] = (byte) i;
            if (sha1_32(buf) == -1412503816) {
                break;
            }
        }
        for (i = 66; i < 86; i++) {
            buf[309] = (byte) i;
            if (sha1_32(buf) == 1423522466) {
                break;
            }
        }
        for (i = -49; i < -29; i++) {
            buf[310] = (byte) i;
            if (sha1_32(buf) == -1067878151) {
                break;
            }
        }
        for (i = 97; i < 113; i++) {
            buf[311] = (byte) i;
            if (sha1_32(buf) == 1555134899) {
                break;
            }
        }
        for (i = 27; i < 52; i++) {
            buf[312] = (byte) i;
            if (sha1_32(buf) == -1497367146) {
                break;
            }
        }
        for (i = 49; i < 53; i++) {
            buf[313] = (byte) i;
            if (sha1_32(buf) == 228851736) {
                break;
            }
        }
        for (i = -105; i < -91; i++) {
            buf[314] = (byte) i;
            if (sha1_32(buf) == 1294489763) {
                break;
            }
        }
        for (i = 62; i < 85; i++) {
            buf[315] = (byte) i;
            if (sha1_32(buf) == -976023267) {
                break;
            }
        }
        for (i = 113; i < 128; i++) {
            buf[316] = (byte) i;
            if (sha1_32(buf) == -1375775268) {
                break;
            }
        }
        for (i = 77; i < 89; i++) {
            buf[317] = (byte) i;
            if (sha1_32(buf) == -1203077608) {
                break;
            }
        }
        for (i = -18; i < -1; i++) {
            buf[318] = (byte) i;
            if (sha1_32(buf) == 240333898) {
                break;
            }
        }
        for (i = 37; i < 60; i++) {
            buf[319] = (byte) i;
            if (sha1_32(buf) == 2096292585) {
                break;
            }
        }
        for (i = -55; i < -43; i++) {
            buf[320] = (byte) i;
            if (sha1_32(buf) == -175483017) {
                break;
            }
        }
        for (i = -88; i < -69; i++) {
            buf[321] = (byte) i;
            if (sha1_32(buf) == -168699932) {
                break;
            }
        }
        for (i = 26; i < 39; i++) {
            buf[322] = (byte) i;
            if (sha1_32(buf) == -163074431) {
                break;
            }
        }
        for (i = 76; i < 93; i++) {
            buf[323] = (byte) i;
            if (sha1_32(buf) == -279858413) {
                break;
            }
        }
        for (i = -42; i < -18; i++) {
            buf[324] = (byte) i;
            if (sha1_32(buf) == 2038139286) {
                break;
            }
        }
        for (i = -38; i < -20; i++) {
            buf[325] = (byte) i;
            if (sha1_32(buf) == -1943974550) {
                break;
            }
        }
        for (i = 96; i < 114; i++) {
            buf[326] = (byte) i;
            if (sha1_32(buf) == 299025413) {
                break;
            }
        }
        for (i = -61; i < -44; i++) {
            buf[327] = (byte) i;
            if (sha1_32(buf) == -207207500) {
                break;
            }
        }
        for (i = -33; i < -22; i++) {
            buf[328] = (byte) i;
            if (sha1_32(buf) == -1434785938) {
                break;
            }
        }
        for (i = 90; i < 109; i++) {
            buf[329] = (byte) i;
            if (sha1_32(buf) == -638529880) {
                break;
            }
        }
        for (i = 59; i < 80; i++) {
            buf[330] = (byte) i;
            if (sha1_32(buf) == -1063728689) {
                break;
            }
        }
        for (i = -12; i < 5; i++) {
            buf[331] = (byte) i;
            if (sha1_32(buf) == -1833800920) {
                break;
            }
        }
        for (i = -40; i < -20; i++) {
            buf[332] = (byte) i;
            if (sha1_32(buf) == -476046488) {
                break;
            }
        }
        for (i = -50; i < -35; i++) {
            buf[333] = (byte) i;
            if (sha1_32(buf) == 718281945) {
                break;
            }
        }
        for (i = 100; i < 106; i++) {
            buf[334] = (byte) i;
            if (sha1_32(buf) == 1645683548) {
                break;
            }
        }
        for (i = 116; i < 125; i++) {
            buf[335] = (byte) i;
            if (sha1_32(buf) == 931715338) {
                break;
            }
        }
        for (i = -33; i < -16; i++) {
            buf[336] = (byte) i;
            if (sha1_32(buf) == 1274174306) {
                break;
            }
        }
        for (i = -35; i < -18; i++) {
            buf[337] = (byte) i;
            if (sha1_32(buf) == -2056821180) {
                break;
            }
        }
        for (i = -49; i < -31; i++) {
            buf[338] = (byte) i;
            if (sha1_32(buf) == -566114447) {
                break;
            }
        }
        for (i = 3; i < 28; i++) {
            buf[339] = (byte) i;
            if (sha1_32(buf) == 1807282525) {
                break;
            }
        }
        for (i = 50; i < 68; i++) {
            buf[340] = (byte) i;
            if (sha1_32(buf) == -1701301710) {
                break;
            }
        }
        for (i = 35; i < 40; i++) {
            buf[341] = (byte) i;
            if (sha1_32(buf) == -1875598593) {
                break;
            }
        }
        for (i = 110; i < 128; i++) {
            buf[342] = (byte) i;
            if (sha1_32(buf) == 597684966) {
                break;
            }
        }
        for (i = -82; i < -77; i++) {
            buf[343] = (byte) i;
            if (sha1_32(buf) == 986487659) {
                break;
            }
        }
        for (i = 92; i < 110; i++) {
            buf[344] = (byte) i;
            if (sha1_32(buf) == 880551128) {
                break;
            }
        }
        for (i = -114; i < -96; i++) {
            buf[345] = (byte) i;
            if (sha1_32(buf) == -182102655) {
                break;
            }
        }
        for (i = -33; i < -17; i++) {
            buf[346] = (byte) i;
            if (sha1_32(buf) == -862503580) {
                break;
            }
        }
        for (i = -128; i < -115; i++) {
            buf[347] = (byte) i;
            if (sha1_32(buf) == -1424929837) {
                break;
            }
        }
        for (i = 29; i < 37; i++) {
            buf[348] = (byte) i;
            if (sha1_32(buf) == -1113821035) {
                break;
            }
        }
        for (i = -58; i < -52; i++) {
            buf[349] = (byte) i;
            if (sha1_32(buf) == -183903895) {
                break;
            }
        }
        for (i = -128; i < -120; i++) {
            buf[350] = (byte) i;
            if (sha1_32(buf) == 978492684) {
                break;
            }
        }
        for (i = 114; i < 128; i++) {
            buf[351] = (byte) i;
            if (sha1_32(buf) == -1794902876) {
                break;
            }
        }
        for (i = -33; i < -10; i++) {
            buf[352] = (byte) i;
            if (sha1_32(buf) == -887024762) {
                break;
            }
        }
        for (i = -128; i < -109; i++) {
            buf[353] = (byte) i;
            if (sha1_32(buf) == 255438780) {
                break;
            }
        }
        for (i = 108; i < 118; i++) {
            buf[354] = (byte) i;
            if (sha1_32(buf) == 1229502625) {
                break;
            }
        }
        for (i = -6; i < 7; i++) {
            buf[355] = (byte) i;
            if (sha1_32(buf) == 1458697616) {
                break;
            }
        }
        for (i = -106; i < -87; i++) {
            buf[356] = (byte) i;
            if (sha1_32(buf) == -1885431568) {
                break;
            }
        }
        for (i = 39; i < 41; i++) {
            buf[357] = (byte) i;
            if (sha1_32(buf) == -730757342) {
                break;
            }
        }
        for (i = -34; i < -13; i++) {
            buf[358] = (byte) i;
            if (sha1_32(buf) == -621119340) {
                break;
            }
        }
        for (i = -28; i < -23; i++) {
            buf[359] = (byte) i;
            if (sha1_32(buf) == -1298964110) {
                break;
            }
        }
        for (i = 34; i < 46; i++) {
            buf[360] = (byte) i;
            if (sha1_32(buf) == 1965627047) {
                break;
            }
        }
        for (i = -128; i < -127; i++) {
            buf[361] = (byte) i;
            if (sha1_32(buf) == -1065238345) {
                break;
            }
        }
        for (i = 27; i < 40; i++) {
            buf[362] = (byte) i;
            if (sha1_32(buf) == -1086824941) {
                break;
            }
        }
        for (i = -98; i < -78; i++) {
            buf[363] = (byte) i;
            if (sha1_32(buf) == -1569153638) {
                break;
            }
        }
        for (i = 20; i < 44; i++) {
            buf[364] = (byte) i;
            if (sha1_32(buf) == -2027549270) {
                break;
            }
        }
        for (i = -115; i < -95; i++) {
            buf[365] = (byte) i;
            if (sha1_32(buf) == -185630907) {
                break;
            }
        }
        for (i = -115; i < -100; i++) {
            buf[366] = (byte) i;
            if (sha1_32(buf) == 1671245536) {
                break;
            }
        }
        for (i = -24; i < -9; i++) {
            buf[367] = (byte) i;
            if (sha1_32(buf) == 1957301997) {
                break;
            }
        }
        for (i = 79; i < 86; i++) {
            buf[368] = (byte) i;
            if (sha1_32(buf) == 865424915) {
                break;
            }
        }
        for (i = 94; i < 120; i++) {
            buf[369] = (byte) i;
            if (sha1_32(buf) == -1068186966) {
                break;
            }
        }
        for (i = 84; i < 108; i++) {
            buf[370] = (byte) i;
            if (sha1_32(buf) == -676633978) {
                break;
            }
        }
        for (i = -128; i < -114; i++) {
            buf[371] = (byte) i;
            if (sha1_32(buf) == 659977711) {
                break;
            }
        }
        for (i = -128; i < -111; i++) {
            buf[372] = (byte) i;
            if (sha1_32(buf) == 1008130265) {
                break;
            }
        }
        for (i = 14; i < 31; i++) {
            buf[373] = (byte) i;
            if (sha1_32(buf) == 1902724574) {
                break;
            }
        }
        for (i = -9; i < 4; i++) {
            buf[374] = (byte) i;
            if (sha1_32(buf) == -541060435) {
                break;
            }
        }
        for (i = -59; i < -51; i++) {
            buf[375] = (byte) i;
            if (sha1_32(buf) == 151957961) {
                break;
            }
        }
        for (i = 72; i < 78; i++) {
            buf[376] = (byte) i;
            if (sha1_32(buf) == 2055562623) {
                break;
            }
        }
        for (i = 108; i < 121; i++) {
            buf[377] = (byte) i;
            if (sha1_32(buf) == 1047275349) {
                break;
            }
        }
        for (i = 0; i < 8; i++) {
            buf[378] = (byte) i;
            if (sha1_32(buf) == 1723623709) {
                break;
            }
        }
        for (i = -122; i < -111; i++) {
            buf[379] = (byte) i;
            if (sha1_32(buf) == 1452504832) {
                break;
            }
        }
        for (i = -93; i < -87; i++) {
            buf[380] = (byte) i;
            if (sha1_32(buf) == 2031250995) {
                break;
            }
        }
        for (i = -11; i < 4; i++) {
            buf[381] = (byte) i;
            if (sha1_32(buf) == 846508428) {
                break;
            }
        }
        for (i = 87; i < 112; i++) {
            buf[382] = (byte) i;
            if (sha1_32(buf) == -1041138851) {
                break;
            }
        }
        for (i = -55; i < -50; i++) {
            buf[383] = (byte) i;
            if (sha1_32(buf) == -851527059) {
                break;
            }
        }
        for (i = 13; i < 28; i++) {
            buf[384] = (byte) i;
            if (sha1_32(buf) == -315960892) {
                break;
            }
        }
        for (i = 80; i < 81; i++) {
            buf[385] = (byte) i;
            if (sha1_32(buf) == -2078336290) {
                break;
            }
        }
        for (i = -15; i < 5; i++) {
            buf[386] = (byte) i;
            if (sha1_32(buf) == 61453668) {
                break;
            }
        }
        for (i = -128; i < -100; i++) {
            buf[387] = (byte) i;
            if (sha1_32(buf) == 129738841) {
                break;
            }
        }
        for (i = -109; i < -100; i++) {
            buf[388] = (byte) i;
            if (sha1_32(buf) == -1067123466) {
                break;
            }
        }
        for (i = -29; i < -14; i++) {
            buf[389] = (byte) i;
            if (sha1_32(buf) == -485290356) {
                break;
            }
        }
        for (i = 75; i < 99; i++) {
            buf[390] = (byte) i;
            if (sha1_32(buf) == -976165009) {
                break;
            }
        }
        for (i = 105; i < 121; i++) {
            buf[391] = (byte) i;
            if (sha1_32(buf) == 1545777458) {
                break;
            }
        }
        for (i = -128; i < -122; i++) {
            buf[392] = (byte) i;
            if (sha1_32(buf) == -2045909481) {
                break;
            }
        }
        for (i = -16; i < -3; i++) {
            buf[393] = (byte) i;
            if (sha1_32(buf) == -927677240) {
                break;
            }
        }
        for (i = 83; i < 107; i++) {
            buf[394] = (byte) i;
            if (sha1_32(buf) == -610016917) {
                break;
            }
        }
        for (i = 58; i < 79; i++) {
            buf[395] = (byte) i;
            if (sha1_32(buf) == -183884752) {
                break;
            }
        }
        for (i = -128; i < -114; i++) {
            buf[396] = (byte) i;
            if (sha1_32(buf) == 1283310549) {
                break;
            }
        }
        for (i = -19; i < 2; i++) {
            buf[397] = (byte) i;
            if (sha1_32(buf) == 842873690) {
                break;
            }
        }
        for (i = 92; i < 94; i++) {
            buf[398] = (byte) i;
            if (sha1_32(buf) == 732560019) {
                break;
            }
        }
        for (i = 55; i < 77; i++) {
            buf[399] = (byte) i;
            if (sha1_32(buf) == -628609198) {
                break;
            }
        }
        for (i = -30; i < -14; i++) {
            buf[400] = (byte) i;
            if (sha1_32(buf) == -1974572973) {
                break;
            }
        }
        for (i = 97; i < 120; i++) {
            buf[401] = (byte) i;
            if (sha1_32(buf) == -179089036) {
                break;
            }
        }
        for (i = 73; i < 84; i++) {
            buf[402] = (byte) i;
            if (sha1_32(buf) == -2031837917) {
                break;
            }
        }
        for (i = 73; i < 84; i++) {
            buf[403] = (byte) i;
            if (sha1_32(buf) == -401323030) {
                break;
            }
        }
        for (i = 104; i < 118; i++) {
            buf[404] = (byte) i;
            if (sha1_32(buf) == -937418081) {
                break;
            }
        }
        for (i = -117; i < -101; i++) {
            buf[405] = (byte) i;
            if (sha1_32(buf) == -1435299237) {
                break;
            }
        }
        for (i = -104; i < -87; i++) {
            buf[406] = (byte) i;
            if (sha1_32(buf) == -149154348) {
                break;
            }
        }
        for (i = -104; i < -94; i++) {
            buf[407] = (byte) i;
            if (sha1_32(buf) == -1259762409) {
                break;
            }
        }
        for (i = 121; i < 128; i++) {
            buf[408] = (byte) i;
            if (sha1_32(buf) == -1872863926) {
                break;
            }
        }
        for (i = -114; i < -106; i++) {
            buf[409] = (byte) i;
            if (sha1_32(buf) == -438520001) {
                break;
            }
        }
        for (i = 27; i < 45; i++) {
            buf[410] = (byte) i;
            if (sha1_32(buf) == -2000393581) {
                break;
            }
        }
        for (i = 93; i < 112; i++) {
            buf[411] = (byte) i;
            if (sha1_32(buf) == 1225654916) {
                break;
            }
        }
        for (i = -58; i < -47; i++) {
            buf[412] = (byte) i;
            if (sha1_32(buf) == 1691844356) {
                break;
            }
        }
        for (i = -106; i < -102; i++) {
            buf[413] = (byte) i;
            if (sha1_32(buf) == 730264035) {
                break;
            }
        }
        for (i = 121; i < 128; i++) {
            buf[414] = (byte) i;
            if (sha1_32(buf) == 903237480) {
                break;
            }
        }
        for (i = -42; i < -17; i++) {
            buf[415] = (byte) i;
            if (sha1_32(buf) == -1305893196) {
                break;
            }
        }
        for (i = 115; i < 128; i++) {
            buf[416] = (byte) i;
            if (sha1_32(buf) == 1649083195) {
                break;
            }
        }
        for (i = 26; i < 39; i++) {
            buf[417] = (byte) i;
            if (sha1_32(buf) == 768530892) {
                break;
            }
        }
        for (i = -61; i < -31; i++) {
            buf[418] = (byte) i;
            if (sha1_32(buf) == 1075674091) {
                break;
            }
        }
        for (i = -117; i < -90; i++) {
            buf[419] = (byte) i;
            if (sha1_32(buf) == 17503424) {
                break;
            }
        }
        for (i = 30; i < 35; i++) {
            buf[420] = (byte) i;
            if (sha1_32(buf) == 1584888633) {
                break;
            }
        }
        for (i = -50; i < -33; i++) {
            buf[421] = (byte) i;
            if (sha1_32(buf) == 302725960) {
                break;
            }
        }
        for (i = 58; i < 81; i++) {
            buf[422] = (byte) i;
            if (sha1_32(buf) == 1534237760) {
                break;
            }
        }
        for (i = -48; i < -33; i++) {
            buf[423] = (byte) i;
            if (sha1_32(buf) == 262548721) {
                break;
            }
        }
        for (i = -128; i < -103; i++) {
            buf[424] = (byte) i;
            if (sha1_32(buf) == -346152014) {
                break;
            }
        }
        for (i = 97; i < 114; i++) {
            buf[425] = (byte) i;
            if (sha1_32(buf) == -939953421) {
                break;
            }
        }
        for (i = -99; i < -80; i++) {
            buf[426] = (byte) i;
            if (sha1_32(buf) == 613147843) {
                break;
            }
        }
        for (i = 61; i < 69; i++) {
            buf[427] = (byte) i;
            if (sha1_32(buf) == 1976937031) {
                break;
            }
        }
        for (i = -114; i < -85; i++) {
            buf[428] = (byte) i;
            if (sha1_32(buf) == 75335881) {
                break;
            }
        }
        for (i = 70; i < 80; i++) {
            buf[429] = (byte) i;
            if (sha1_32(buf) == -1275235094) {
                break;
            }
        }
        for (i = 74; i < 91; i++) {
            buf[430] = (byte) i;
            if (sha1_32(buf) == 1505545003) {
                break;
            }
        }
        for (i = 57; i < 66; i++) {
            buf[431] = (byte) i;
            if (sha1_32(buf) == 1093576128) {
                break;
            }
        }
        for (i = -118; i < -92; i++) {
            buf[432] = (byte) i;
            if (sha1_32(buf) == -529246081) {
                break;
            }
        }
        for (i = -46; i < -43; i++) {
            buf[433] = (byte) i;
            if (sha1_32(buf) == 187835503) {
                break;
            }
        }
        for (i = -69; i < -58; i++) {
            buf[434] = (byte) i;
            if (sha1_32(buf) == 675826571) {
                break;
            }
        }
        for (i = 43; i < 49; i++) {
            buf[435] = (byte) i;
            if (sha1_32(buf) == -1237399482) {
                break;
            }
        }
        for (i = 51; i < 57; i++) {
            buf[436] = (byte) i;
            if (sha1_32(buf) == 2020701011) {
                break;
            }
        }
        for (i = 0; i < 14; i++) {
            buf[437] = (byte) i;
            if (sha1_32(buf) == 201331139) {
                break;
            }
        }
        for (i = 32; i < 49; i++) {
            buf[438] = (byte) i;
            if (sha1_32(buf) == 1609654962) {
                break;
            }
        }
        for (i = -85; i < -73; i++) {
            buf[439] = (byte) i;
            if (sha1_32(buf) == 1587821419) {
                break;
            }
        }
        for (i = -51; i < -26; i++) {
            buf[440] = (byte) i;
            if (sha1_32(buf) == 588590618) {
                break;
            }
        }
        for (i = 66; i < 84; i++) {
            buf[441] = (byte) i;
            if (sha1_32(buf) == 1707205094) {
                break;
            }
        }
        for (i = 53; i < 73; i++) {
            buf[442] = (byte) i;
            if (sha1_32(buf) == 266043695) {
                break;
            }
        }
        for (i = -115; i < -104; i++) {
            buf[443] = (byte) i;
            if (sha1_32(buf) == 2079970907) {
                break;
            }
        }
        for (i = -82; i < -70; i++) {
            buf[444] = (byte) i;
            if (sha1_32(buf) == 430292204) {
                break;
            }
        }
        for (i = -85; i < -74; i++) {
            buf[445] = (byte) i;
            if (sha1_32(buf) == -183330705) {
                break;
            }
        }
        for (i = 108; i < 125; i++) {
            buf[446] = (byte) i;
            if (sha1_32(buf) == 661340908) {
                break;
            }
        }
        for (i = 33; i < 54; i++) {
            buf[447] = (byte) i;
            if (sha1_32(buf) == 1985966464) {
                break;
            }
        }
        for (i = -12; i < 7; i++) {
            buf[448] = (byte) i;
            if (sha1_32(buf) == 1560102766) {
                break;
            }
        }
        for (i = -36; i < -16; i++) {
            buf[449] = (byte) i;
            if (sha1_32(buf) == -2033816080) {
                break;
            }
        }
        for (i = -127; i < -109; i++) {
            buf[450] = (byte) i;
            if (sha1_32(buf) == -247622709) {
                break;
            }
        }
        for (i = 72; i < 88; i++) {
            buf[451] = (byte) i;
            if (sha1_32(buf) == 215804645) {
                break;
            }
        }
        for (i = 12; i < 23; i++) {
            buf[452] = (byte) i;
            if (sha1_32(buf) == -715601912) {
                break;
            }
        }
        for (i = -122; i < -112; i++) {
            buf[453] = (byte) i;
            if (sha1_32(buf) == -1432250828) {
                break;
            }
        }
        for (i = -55; i < -43; i++) {
            buf[454] = (byte) i;
            if (sha1_32(buf) == -1600173222) {
                break;
            }
        }
        for (i = -86; i < -72; i++) {
            buf[455] = (byte) i;
            if (sha1_32(buf) == -1270569738) {
                break;
            }
        }
        for (i = 62; i < 81; i++) {
            buf[456] = (byte) i;
            if (sha1_32(buf) == 427047074) {
                break;
            }
        }
        for (i = 23; i < 37; i++) {
            buf[457] = (byte) i;
            if (sha1_32(buf) == 1917929963) {
                break;
            }
        }
        for (i = 66; i < 71; i++) {
            buf[458] = (byte) i;
            if (sha1_32(buf) == -182538227) {
                break;
            }
        }
        for (i = -96; i < -73; i++) {
            buf[459] = (byte) i;
            if (sha1_32(buf) == 339555025) {
                break;
            }
        }
        for (i = -1; i < 11; i++) {
            buf[460] = (byte) i;
            if (sha1_32(buf) == 672086415) {
                break;
            }
        }
        for (i = -128; i < -109; i++) {
            buf[461] = (byte) i;
            if (sha1_32(buf) == -748452634) {
                break;
            }
        }
        for (i = -68; i < -50; i++) {
            buf[462] = (byte) i;
            if (sha1_32(buf) == -1444810187) {
                break;
            }
        }
        for (i = -128; i < -110; i++) {
            buf[463] = (byte) i;
            if (sha1_32(buf) == 1752830072) {
                break;
            }
        }
        for (i = -93; i < -78; i++) {
            buf[464] = (byte) i;
            if (sha1_32(buf) == 618041887) {
                break;
            }
        }
        for (i = -51; i < -37; i++) {
            buf[465] = (byte) i;
            if (sha1_32(buf) == -362263097) {
                break;
            }
        }
        for (i = -68; i < -60; i++) {
            buf[466] = (byte) i;
            if (sha1_32(buf) == -203754763) {
                break;
            }
        }
        for (i = 17; i < 26; i++) {
            buf[467] = (byte) i;
            if (sha1_32(buf) == 1836578147) {
                break;
            }
        }
        for (i = -83; i < -78; i++) {
            buf[468] = (byte) i;
            if (sha1_32(buf) == 1368277513) {
                break;
            }
        }
        for (i = -102; i < -85; i++) {
            buf[469] = (byte) i;
            if (sha1_32(buf) == 340669387) {
                break;
            }
        }
        for (i = -81; i < -61; i++) {
            buf[470] = (byte) i;
            if (sha1_32(buf) == -1869992038) {
                break;
            }
        }
        for (i = 28; i < 34; i++) {
            buf[471] = (byte) i;
            if (sha1_32(buf) == -859710105) {
                break;
            }
        }
        for (i = 26; i < 44; i++) {
            buf[472] = (byte) i;
            if (sha1_32(buf) == 1124655951) {
                break;
            }
        }
        for (i = -27; i < 0; i++) {
            buf[473] = (byte) i;
            if (sha1_32(buf) == -845731490) {
                break;
            }
        }
        for (i = 53; i < 69; i++) {
            buf[474] = (byte) i;
            if (sha1_32(buf) == -1479722031) {
                break;
            }
        }
        for (i = -68; i < -65; i++) {
            buf[475] = (byte) i;
            if (sha1_32(buf) == 492716403) {
                break;
            }
        }
        for (i = 32; i < 38; i++) {
            buf[476] = (byte) i;
            if (sha1_32(buf) == 1091883403) {
                break;
            }
        }
        for (i = -74; i < -58; i++) {
            buf[477] = (byte) i;
            if (sha1_32(buf) == -691662425) {
                break;
            }
        }
        for (i = 76; i < 97; i++) {
            buf[478] = (byte) i;
            if (sha1_32(buf) == 1416188566) {
                break;
            }
        }
        for (i = 107; i < 125; i++) {
            buf[479] = (byte) i;
            if (sha1_32(buf) == 291914797) {
                break;
            }
        }
        for (i = 44; i < 52; i++) {
            buf[480] = (byte) i;
            if (sha1_32(buf) == -1422250739) {
                break;
            }
        }
        for (i = -64; i < -45; i++) {
            buf[481] = (byte) i;
            if (sha1_32(buf) == -2131096188) {
                break;
            }
        }
        for (i = 66; i < 82; i++) {
            buf[482] = (byte) i;
            if (sha1_32(buf) == 1886017593) {
                break;
            }
        }
        for (i = -35; i < -23; i++) {
            buf[483] = (byte) i;
            if (sha1_32(buf) == 603906331) {
                break;
            }
        }
        for (i = 96; i < 116; i++) {
            buf[484] = (byte) i;
            if (sha1_32(buf) == -415951903) {
                break;
            }
        }
        for (i = -87; i < -73; i++) {
            buf[485] = (byte) i;
            if (sha1_32(buf) == -475475083) {
                break;
            }
        }
        for (i = 57; i < 67; i++) {
            buf[486] = (byte) i;
            if (sha1_32(buf) == 377567522) {
                break;
            }
        }
        for (i = -59; i < -41; i++) {
            buf[487] = (byte) i;
            if (sha1_32(buf) == -846120631) {
                break;
            }
        }
        for (i = -116; i < -89; i++) {
            buf[488] = (byte) i;
            if (sha1_32(buf) == -298412810) {
                break;
            }
        }
        for (i = -114; i < -97; i++) {
            buf[489] = (byte) i;
            if (sha1_32(buf) == 770786948) {
                break;
            }
        }
        for (i = -22; i < -4; i++) {
            buf[490] = (byte) i;
            if (sha1_32(buf) == 949781623) {
                break;
            }
        }
        for (i = -45; i < -23; i++) {
            buf[491] = (byte) i;
            if (sha1_32(buf) == -1398013912) {
                break;
            }
        }
        for (i = 90; i < 104; i++) {
            buf[492] = (byte) i;
            if (sha1_32(buf) == -11547742) {
                break;
            }
        }
        for (i = 70; i < 87; i++) {
            buf[493] = (byte) i;
            if (sha1_32(buf) == -892865442) {
                break;
            }
        }
        for (i = 89; i < 111; i++) {
            buf[494] = (byte) i;
            if (sha1_32(buf) == -564391489) {
                break;
            }
        }
        for (i = 41; i < 48; i++) {
            buf[495] = (byte) i;
            if (sha1_32(buf) == -412881472) {
                break;
            }
        }
        for (i = 106; i < 115; i++) {
            buf[496] = (byte) i;
            if (sha1_32(buf) == -927841862) {
                break;
            }
        }
        for (i = 71; i < 91; i++) {
            buf[497] = (byte) i;
            if (sha1_32(buf) == 526854556) {
                break;
            }
        }
        for (i = 13; i < 22; i++) {
            buf[498] = (byte) i;
            if (sha1_32(buf) == -1996662114) {
                break;
            }
        }
        for (i = -128; i < -110; i++) {
            buf[499] = (byte) i;
            if (sha1_32(buf) == 83733698) {
                break;
            }
        }
        for (i = 48; i < 63; i++) {
            buf[500] = (byte) i;
            if (sha1_32(buf) == -69611934) {
                break;
            }
        }
        for (i = -102; i < -87; i++) {
            buf[501] = (byte) i;
            if (sha1_32(buf) == 447892603) {
                break;
            }
        }
        for (i = 36; i < 51; i++) {
            buf[502] = (byte) i;
            if (sha1_32(buf) == -170532016) {
                break;
            }
        }
        for (i = -26; i < -11; i++) {
            buf[503] = (byte) i;
            if (sha1_32(buf) == -1131849182) {
                break;
            }
        }
        for (i = -128; i < -119; i++) {
            buf[504] = (byte) i;
            if (sha1_32(buf) == 1123170121) {
                break;
            }
        }
        for (i = 39; i < 40; i++) {
            buf[505] = (byte) i;
            if (sha1_32(buf) == 413566805) {
                break;
            }
        }
        for (i = -15; i < -3; i++) {
            buf[506] = (byte) i;
            if (sha1_32(buf) == 1320306320) {
                break;
            }
        }
        for (i = 77; i < 86; i++) {
            buf[507] = (byte) i;
            if (sha1_32(buf) == -603405473) {
                break;
            }
        }
        for (i = -104; i < -92; i++) {
            buf[508] = (byte) i;
            if (sha1_32(buf) == 1755785480) {
                break;
            }
        }
        for (i = -75; i < -58; i++) {
            buf[509] = (byte) i;
            if (sha1_32(buf) == 445389250) {
                break;
            }
        }
        for (i = -69; i < -43; i++) {
            buf[510] = (byte) i;
            if (sha1_32(buf) == -461386693) {
                break;
            }
        }
        for (i = 33; i < 47; i++) {
            buf[511] = (byte) i;
            if (sha1_32(buf) == -1896529395) {
                break;
            }
        }
        for (i = 110; i < 128; i++) {
            buf[512] = (byte) i;
            if (sha1_32(buf) == 1241151470) {
                break;
            }
        }
        for (i = 31; i < 51; i++) {
            buf[513] = (byte) i;
            if (sha1_32(buf) == -264442671) {
                break;
            }
        }
        for (i = -106; i < -98; i++) {
            buf[514] = (byte) i;
            if (sha1_32(buf) == -1875071519) {
                break;
            }
        }
        for (i = 41; i < 57; i++) {
            buf[515] = (byte) i;
            if (sha1_32(buf) == -331465630) {
                break;
            }
        }
        for (i = 93; i < 115; i++) {
            buf[516] = (byte) i;
            if (sha1_32(buf) == -820920512) {
                break;
            }
        }
        for (i = -103; i < -83; i++) {
            buf[517] = (byte) i;
            if (sha1_32(buf) == 1189454663) {
                break;
            }
        }
        for (i = -103; i < -98; i++) {
            buf[518] = (byte) i;
            if (sha1_32(buf) == -1279803457) {
                break;
            }
        }
        for (i = -100; i < -87; i++) {
            buf[519] = (byte) i;
            if (sha1_32(buf) == -1606241310) {
                break;
            }
        }
        for (i = 43; i < 47; i++) {
            buf[520] = (byte) i;
            if (sha1_32(buf) == 821960562) {
                break;
            }
        }
        for (i = 5; i < 24; i++) {
            buf[521] = (byte) i;
            if (sha1_32(buf) == 1653337836) {
                break;
            }
        }
        for (i = 15; i < 26; i++) {
            buf[522] = (byte) i;
            if (sha1_32(buf) == -860042151) {
                break;
            }
        }
        for (i = 103; i < 117; i++) {
            buf[523] = (byte) i;
            if (sha1_32(buf) == -102336013) {
                break;
            }
        }
        for (i = -109; i < -95; i++) {
            buf[524] = (byte) i;
            if (sha1_32(buf) == -377918930) {
                break;
            }
        }
        for (i = 105; i < 125; i++) {
            buf[525] = (byte) i;
            if (sha1_32(buf) == -1303440109) {
                break;
            }
        }
        for (i = -55; i < -31; i++) {
            buf[526] = (byte) i;
            if (sha1_32(buf) == 482378563) {
                break;
            }
        }
        for (i = -7; i < 2; i++) {
            buf[527] = (byte) i;
            if (sha1_32(buf) == 1143845436) {
                break;
            }
        }
        for (i = -123; i < -105; i++) {
            buf[528] = (byte) i;
            if (sha1_32(buf) == 323459675) {
                break;
            }
        }
        for (i = -19; i < -3; i++) {
            buf[529] = (byte) i;
            if (sha1_32(buf) == 564857454) {
                break;
            }
        }
        for (i = -95; i < -77; i++) {
            buf[530] = (byte) i;
            if (sha1_32(buf) == 911961364) {
                break;
            }
        }
        for (i = 76; i < 93; i++) {
            buf[531] = (byte) i;
            if (sha1_32(buf) == -1970459679) {
                break;
            }
        }
        for (i = 31; i < 34; i++) {
            buf[532] = (byte) i;
            if (sha1_32(buf) == -648054732) {
                break;
            }
        }
        for (i = 59; i < 61; i++) {
            buf[533] = (byte) i;
            if (sha1_32(buf) == 2106430357) {
                break;
            }
        }
        for (i = 80; i < 88; i++) {
            buf[534] = (byte) i;
            if (sha1_32(buf) == 787954203) {
                break;
            }
        }
        for (i = -125; i < -122; i++) {
            buf[535] = (byte) i;
            if (sha1_32(buf) == -437274416) {
                break;
            }
        }
        for (i = -60; i < -46; i++) {
            buf[536] = (byte) i;
            if (sha1_32(buf) == 1635030608) {
                break;
            }
        }
        for (i = -44; i < -32; i++) {
            buf[537] = (byte) i;
            if (sha1_32(buf) == -1417851734) {
                break;
            }
        }
        for (i = 88; i < 115; i++) {
            buf[538] = (byte) i;
            if (sha1_32(buf) == -361274048) {
                break;
            }
        }
        for (i = -77; i < -67; i++) {
            buf[539] = (byte) i;
            if (sha1_32(buf) == 457129057) {
                break;
            }
        }
        for (i = -71; i < -63; i++) {
            buf[540] = (byte) i;
            if (sha1_32(buf) == 1092575475) {
                break;
            }
        }
        for (i = -128; i < -116; i++) {
            buf[541] = (byte) i;
            if (sha1_32(buf) == 1791885887) {
                break;
            }
        }
        for (i = -55; i < -52; i++) {
            buf[542] = (byte) i;
            if (sha1_32(buf) == -1425634895) {
                break;
            }
        }
        for (i = -83; i < -58; i++) {
            buf[543] = (byte) i;
            if (sha1_32(buf) == 1268518590) {
                break;
            }
        }
        for (i = 72; i < 76; i++) {
            buf[544] = (byte) i;
            if (sha1_32(buf) == -1284374876) {
                break;
            }
        }
        for (i = 51; i < 64; i++) {
            buf[545] = (byte) i;
            if (sha1_32(buf) == 236714940) {
                break;
            }
        }
        for (i = 85; i < 98; i++) {
            buf[546] = (byte) i;
            if (sha1_32(buf) == 1773459318) {
                break;
            }
        }
        for (i = -128; i < -122; i++) {
            buf[547] = (byte) i;
            if (sha1_32(buf) == 590083657) {
                break;
            }
        }
        for (i = 19; i < 34; i++) {
            buf[548] = (byte) i;
            if (sha1_32(buf) == 2107314691) {
                break;
            }
        }
        for (i = 53; i < 71; i++) {
            buf[549] = (byte) i;
            if (sha1_32(buf) == -1788518736) {
                break;
            }
        }
        for (i = 91; i < 118; i++) {
            buf[550] = (byte) i;
            if (sha1_32(buf) == -365089595) {
                break;
            }
        }
        for (i = 48; i < 63; i++) {
            buf[551] = (byte) i;
            if (sha1_32(buf) == -631212109) {
                break;
            }
        }
        for (i = 89; i < 106; i++) {
            buf[552] = (byte) i;
            if (sha1_32(buf) == -165928851) {
                break;
            }
        }
        for (i = 1; i < 21; i++) {
            buf[553] = (byte) i;
            if (sha1_32(buf) == 593216927) {
                break;
            }
        }
        for (i = -100; i < -76; i++) {
            buf[554] = (byte) i;
            if (sha1_32(buf) == 1211491612) {
                break;
            }
        }
        for (i = 67; i < 79; i++) {
            buf[555] = (byte) i;
            if (sha1_32(buf) == -1241155547) {
                break;
            }
        }
        for (i = -97; i < -89; i++) {
            buf[556] = (byte) i;
            if (sha1_32(buf) == -1227741452) {
                break;
            }
        }
        for (i = -77; i < -64; i++) {
            buf[557] = (byte) i;
            if (sha1_32(buf) == 1082221486) {
                break;
            }
        }
        for (i = 78; i < 94; i++) {
            buf[558] = (byte) i;
            if (sha1_32(buf) == -952396428) {
                break;
            }
        }
        for (i = -30; i < -14; i++) {
            buf[559] = (byte) i;
            if (sha1_32(buf) == 1320270516) {
                break;
            }
        }
        for (i = -128; i < -113; i++) {
            buf[560] = (byte) i;
            if (sha1_32(buf) == 1250769947) {
                break;
            }
        }
        for (i = 117; i < 128; i++) {
            buf[561] = (byte) i;
            if (sha1_32(buf) == -2019858869) {
                break;
            }
        }
        for (i = 85; i < 108; i++) {
            buf[562] = (byte) i;
            if (sha1_32(buf) == 1502702017) {
                break;
            }
        }
        for (i = 69; i < 78; i++) {
            buf[563] = (byte) i;
            if (sha1_32(buf) == 1206892426) {
                break;
            }
        }
        for (i = -19; i < -9; i++) {
            buf[564] = (byte) i;
            if (sha1_32(buf) == -1547331248) {
                break;
            }
        }
        for (i = 11; i < 27; i++) {
            buf[565] = (byte) i;
            if (sha1_32(buf) == -1743989892) {
                break;
            }
        }
        for (i = 17; i < 34; i++) {
            buf[566] = (byte) i;
            if (sha1_32(buf) == -1775775567) {
                break;
            }
        }
        for (i = 81; i < 103; i++) {
            buf[567] = (byte) i;
            if (sha1_32(buf) == 1977886737) {
                break;
            }
        }
        for (i = -18; i < 7; i++) {
            buf[568] = (byte) i;
            if (sha1_32(buf) == 2069330707) {
                break;
            }
        }
        for (i = -102; i < -74; i++) {
            buf[569] = (byte) i;
            if (sha1_32(buf) == -566894074) {
                break;
            }
        }
        for (i = -128; i < -116; i++) {
            buf[570] = (byte) i;
            if (sha1_32(buf) == -376689460) {
                break;
            }
        }
        for (i = -120; i < -97; i++) {
            buf[571] = (byte) i;
            if (sha1_32(buf) == 9024100) {
                break;
            }
        }
        for (i = -61; i < -40; i++) {
            buf[572] = (byte) i;
            if (sha1_32(buf) == 293756859) {
                break;
            }
        }
        for (i = -107; i < -87; i++) {
            buf[573] = (byte) i;
            if (sha1_32(buf) == -1430584045) {
                break;
            }
        }
        for (i = -7; i < 5; i++) {
            buf[574] = (byte) i;
            if (sha1_32(buf) == -1944050629) {
                break;
            }
        }
        for (i = -21; i < -5; i++) {
            buf[575] = (byte) i;
            if (sha1_32(buf) == 291184437) {
                break;
            }
        }
        for (i = 120; i < 128; i++) {
            buf[576] = (byte) i;
            if (sha1_32(buf) == 20095394) {
                break;
            }
        }
        for (i = 116; i < 128; i++) {
            buf[577] = (byte) i;
            if (sha1_32(buf) == 198689213) {
                break;
            }
        }
        for (i = -79; i < -62; i++) {
            buf[578] = (byte) i;
            if (sha1_32(buf) == -1089222799) {
                break;
            }
        }
        for (i = -81; i < -67; i++) {
            buf[579] = (byte) i;
            if (sha1_32(buf) == -1373512101) {
                break;
            }
        }
        for (i = 109; i < 128; i++) {
            buf[580] = (byte) i;
            if (sha1_32(buf) == 614160546) {
                break;
            }
        }
        for (i = -4; i < 6; i++) {
            buf[581] = (byte) i;
            if (sha1_32(buf) == 983744002) {
                break;
            }
        }
        for (i = 125; i < 128; i++) {
            buf[582] = (byte) i;
            if (sha1_32(buf) == 827288165) {
                break;
            }
        }
        for (i = 34; i < 53; i++) {
            buf[583] = (byte) i;
            if (sha1_32(buf) == 2739421) {
                break;
            }
        }
        for (i = 57; i < 66; i++) {
            buf[584] = (byte) i;
            if (sha1_32(buf) == -1020183930) {
                break;
            }
        }
        for (i = -128; i < -118; i++) {
            buf[585] = (byte) i;
            if (sha1_32(buf) == 537498747) {
                break;
            }
        }
        for (i = -101; i < -89; i++) {
            buf[586] = (byte) i;
            if (sha1_32(buf) == -1879343035) {
                break;
            }
        }
        for (i = -33; i < -12; i++) {
            buf[587] = (byte) i;
            if (sha1_32(buf) == -783761294) {
                break;
            }
        }
        for (i = -16; i < 0; i++) {
            buf[588] = (byte) i;
            if (sha1_32(buf) == 457924415) {
                break;
            }
        }
        for (i = 34; i < 49; i++) {
            buf[589] = (byte) i;
            if (sha1_32(buf) == 748730823) {
                break;
            }
        }
        for (i = 0; i < 13; i++) {
            buf[590] = (byte) i;
            if (sha1_32(buf) == 748730823) {
                break;
            }
        }
        for (i = -115; i < -112; i++) {
            buf[591] = (byte) i;
            if (sha1_32(buf) == -851865095) {
                break;
            }
        }
        for (i = -124; i < -114; i++) {
            buf[592] = (byte) i;
            if (sha1_32(buf) == 81734952) {
                break;
            }
        }
        for (i = 5; i < 17; i++) {
            buf[593] = (byte) i;
            if (sha1_32(buf) == -1681903422) {
                break;
            }
        }
        for (i = 65; i < 91; i++) {
            buf[594] = (byte) i;
            if (sha1_32(buf) == -1891296693) {
                break;
            }
        }
        for (i = 25; i < 43; i++) {
            buf[595] = (byte) i;
            if (sha1_32(buf) == -1664262594) {
                break;
            }
        }
        for (i = -11; i < 13; i++) {
            buf[596] = (byte) i;
            if (sha1_32(buf) == 978118279) {
                break;
            }
        }
        for (i = 77; i < 86; i++) {
            buf[597] = (byte) i;
            if (sha1_32(buf) == -1929468538) {
                break;
            }
        }
        for (i = -128; i < -122; i++) {
            buf[598] = (byte) i;
            if (sha1_32(buf) == -1661726713) {
                break;
            }
        }
        for (i = -57; i < -50; i++) {
            buf[599] = (byte) i;
            if (sha1_32(buf) == 890014814) {
                break;
            }
        }
        for (i = -83; i < -81; i++) {
            buf[600] = (byte) i;
            if (sha1_32(buf) == 851827460) {
                break;
            }
        }
        for (i = -58; i < -43; i++) {
            buf[601] = (byte) i;
            if (sha1_32(buf) == -1574245721) {
                break;
            }
        }
        for (i = 2; i < 26; i++) {
            buf[602] = (byte) i;
            if (sha1_32(buf) == 1115944555) {
                break;
            }
        }
        for (i = -96; i < -73; i++) {
            buf[603] = (byte) i;
            if (sha1_32(buf) == 1575174185) {
                break;
            }
        }
        for (i = 116; i < 128; i++) {
            buf[604] = (byte) i;
            if (sha1_32(buf) == -763126616) {
                break;
            }
        }
        for (i = -75; i < -65; i++) {
            buf[605] = (byte) i;
            if (sha1_32(buf) == 1929547750) {
                break;
            }
        }
        for (i = -58; i < -56; i++) {
            buf[606] = (byte) i;
            if (sha1_32(buf) == -1878363795) {
                break;
            }
        }
        for (i = 80; i < 85; i++) {
            buf[607] = (byte) i;
            if (sha1_32(buf) == -1821216279) {
                break;
            }
        }
        for (i = 15; i < 18; i++) {
            buf[608] = (byte) i;
            if (sha1_32(buf) == -1660898604) {
                break;
            }
        }
        for (i = -9; i < 18; i++) {
            buf[609] = (byte) i;
            if (sha1_32(buf) == 1704412092) {
                break;
            }
        }
        for (i = 39; i < 62; i++) {
            buf[610] = (byte) i;
            if (sha1_32(buf) == -577535740) {
                break;
            }
        }
        for (i = -27; i < -11; i++) {
            buf[611] = (byte) i;
            if (sha1_32(buf) == -981646570) {
                break;
            }
        }
        for (i = -128; i < -112; i++) {
            buf[612] = (byte) i;
            if (sha1_32(buf) == -1281464404) {
                break;
            }
        }
        for (i = -128; i < -114; i++) {
            buf[613] = (byte) i;
            if (sha1_32(buf) == -1727777035) {
                break;
            }
        }
        for (i = 16; i < 38; i++) {
            buf[614] = (byte) i;
            if (sha1_32(buf) == -1885995169) {
                break;
            }
        }
        for (i = -100; i < -90; i++) {
            buf[615] = (byte) i;
            if (sha1_32(buf) == -1613080365) {
                break;
            }
        }
        for (i = 98; i < 113; i++) {
            buf[616] = (byte) i;
            if (sha1_32(buf) == 2133546843) {
                break;
            }
        }
        for (i = 35; i < 39; i++) {
            buf[617] = (byte) i;
            if (sha1_32(buf) == 624535285) {
                break;
            }
        }
        for (i = -100; i < -80; i++) {
            buf[618] = (byte) i;
            if (sha1_32(buf) == 1811940859) {
                break;
            }
        }
        for (i = 5; i < 29; i++) {
            buf[619] = (byte) i;
            if (sha1_32(buf) == -842211678) {
                break;
            }
        }
        for (i = 58; i < 74; i++) {
            buf[620] = (byte) i;
            if (sha1_32(buf) == -689356824) {
                break;
            }
        }
        for (i = 76; i < 82; i++) {
            buf[621] = (byte) i;
            if (sha1_32(buf) == 807656755) {
                break;
            }
        }
        for (i = -97; i < -90; i++) {
            buf[622] = (byte) i;
            if (sha1_32(buf) == -2103656011) {
                break;
            }
        }
        for (i = 96; i < 104; i++) {
            buf[623] = (byte) i;
            if (sha1_32(buf) == -760726926) {
                break;
            }
        }
        for (i = 90; i < 109; i++) {
            buf[624] = (byte) i;
            if (sha1_32(buf) == 1444136754) {
                break;
            }
        }
        for (i = -16; i < 13; i++) {
            buf[625] = (byte) i;
            if (sha1_32(buf) == 27889345) {
                break;
            }
        }
        for (i = -128; i < -115; i++) {
            buf[626] = (byte) i;
            if (sha1_32(buf) == -1645193310) {
                break;
            }
        }
        for (i = -67; i < -55; i++) {
            buf[627] = (byte) i;
            if (sha1_32(buf) == -66884642) {
                break;
            }
        }
        for (i = 51; i < 61; i++) {
            buf[628] = (byte) i;
            if (sha1_32(buf) == 1963322156) {
                break;
            }
        }
        for (i = -126; i < -101; i++) {
            buf[629] = (byte) i;
            if (sha1_32(buf) == -356006679) {
                break;
            }
        }
        for (i = 59; i < 85; i++) {
            buf[630] = (byte) i;
            if (sha1_32(buf) == -1419895216) {
                break;
            }
        }
        for (i = -61; i < -46; i++) {
            buf[631] = (byte) i;
            if (sha1_32(buf) == -1934877750) {
                break;
            }
        }
        for (i = 6; i < 35; i++) {
            buf[632] = (byte) i;
            if (sha1_32(buf) == 49768842) {
                break;
            }
        }
        for (i = 52; i < 68; i++) {
            buf[633] = (byte) i;
            if (sha1_32(buf) == -67658998) {
                break;
            }
        }
        for (i = -24; i < -15; i++) {
            buf[634] = (byte) i;
            if (sha1_32(buf) == 1246023107) {
                break;
            }
        }
        for (i = -108; i < -99; i++) {
            buf[635] = (byte) i;
            if (sha1_32(buf) == -2125810108) {
                break;
            }
        }
        for (i = -99; i < -85; i++) {
            buf[636] = (byte) i;
            if (sha1_32(buf) == -1350797042) {
                break;
            }
        }
        for (i = 97; i < 104; i++) {
            buf[637] = (byte) i;
            if (sha1_32(buf) == -1198071223) {
                break;
            }
        }
        for (i = 59; i < 62; i++) {
            buf[638] = (byte) i;
            if (sha1_32(buf) == 903378332) {
                break;
            }
        }
        for (i = -127; i < -114; i++) {
            buf[639] = (byte) i;
            if (sha1_32(buf) == -613598797) {
                break;
            }
        }
        for (i = 21; i < 31; i++) {
            buf[640] = (byte) i;
            if (sha1_32(buf) == -1424539121) {
                break;
            }
        }
        for (i = -46; i < -26; i++) {
            buf[641] = (byte) i;
            if (sha1_32(buf) == -1360159017) {
                break;
            }
        }
        for (i = 117; i < 128; i++) {
            buf[642] = (byte) i;
            if (sha1_32(buf) == -1395060380) {
                break;
            }
        }
        for (i = 45; i < 53; i++) {
            buf[643] = (byte) i;
            if (sha1_32(buf) == 207281439) {
                break;
            }
        }
        for (i = 101; i < 128; i++) {
            buf[644] = (byte) i;
            if (sha1_32(buf) == -1707067316) {
                break;
            }
        }
        for (i = 100; i < 112; i++) {
            buf[645] = (byte) i;
            if (sha1_32(buf) == 762407520) {
                break;
            }
        }
        for (i = -40; i < -28; i++) {
            buf[646] = (byte) i;
            if (sha1_32(buf) == 1366150711) {
                break;
            }
        }
        for (i = -61; i < -41; i++) {
            buf[647] = (byte) i;
            if (sha1_32(buf) == -1003502063) {
                break;
            }
        }
        for (i = -20; i < -3; i++) {
            buf[648] = (byte) i;
            if (sha1_32(buf) == 888065432) {
                break;
            }
        }
        for (i = -90; i < -68; i++) {
            buf[649] = (byte) i;
            if (sha1_32(buf) == -1555964002) {
                break;
            }
        }
        for (i = 23; i < 40; i++) {
            buf[650] = (byte) i;
            if (sha1_32(buf) == -990479115) {
                break;
            }
        }
        for (i = -34; i < -8; i++) {
            buf[651] = (byte) i;
            if (sha1_32(buf) == -1642532556) {
                break;
            }
        }
        for (i = 96; i < 112; i++) {
            buf[652] = (byte) i;
            if (sha1_32(buf) == -1561295790) {
                break;
            }
        }
        for (i = -73; i < -60; i++) {
            buf[653] = (byte) i;
            if (sha1_32(buf) == 1317093816) {
                break;
            }
        }
        for (i = 50; i < 73; i++) {
            buf[654] = (byte) i;
            if (sha1_32(buf) == -2062434351) {
                break;
            }
        }
        for (i = -90; i < -62; i++) {
            buf[655] = (byte) i;
            if (sha1_32(buf) == -1607016679) {
                break;
            }
        }
        for (i = -27; i < -11; i++) {
            buf[656] = (byte) i;
            if (sha1_32(buf) == 1582700248) {
                break;
            }
        }
        for (i = -10; i < 4; i++) {
            buf[657] = (byte) i;
            if (sha1_32(buf) == 1460296373) {
                break;
            }
        }
        for (i = -95; i < -77; i++) {
            buf[658] = (byte) i;
            if (sha1_32(buf) == 703424462) {
                break;
            }
        }
        for (i = 23; i < 30; i++) {
            buf[659] = (byte) i;
            if (sha1_32(buf) == -698915084) {
                break;
            }
        }
        for (i = 49; i < 55; i++) {
            buf[660] = (byte) i;
            if (sha1_32(buf) == -1810753664) {
                break;
            }
        }
        for (i = -41; i < -28; i++) {
            buf[661] = (byte) i;
            if (sha1_32(buf) == -680796364) {
                break;
            }
        }
        for (i = -110; i < -99; i++) {
            buf[662] = (byte) i;
            if (sha1_32(buf) == -2078077115) {
                break;
            }
        }
        for (i = -63; i < -52; i++) {
            buf[663] = (byte) i;
            if (sha1_32(buf) == -1315148119) {
                break;
            }
        }
        for (i = -105; i < -88; i++) {
            buf[664] = (byte) i;
            if (sha1_32(buf) == -202003109) {
                break;
            }
        }
        for (i = 11; i < 37; i++) {
            buf[665] = (byte) i;
            if (sha1_32(buf) == 236164120) {
                break;
            }
        }
        for (i = 108; i < 124; i++) {
            buf[666] = (byte) i;
            if (sha1_32(buf) == 222656275) {
                break;
            }
        }
        for (i = 61; i < 86; i++) {
            buf[667] = (byte) i;
            if (sha1_32(buf) == -2024611296) {
                break;
            }
        }
        for (i = 91; i < 107; i++) {
            buf[668] = (byte) i;
            if (sha1_32(buf) == -1799383737) {
                break;
            }
        }
        for (i = -20; i < -8; i++) {
            buf[669] = (byte) i;
            if (sha1_32(buf) == 122691057) {
                break;
            }
        }
        for (i = -65; i < -47; i++) {
            buf[670] = (byte) i;
            if (sha1_32(buf) == 1793177736) {
                break;
            }
        }
        for (i = -23; i < -16; i++) {
            buf[671] = (byte) i;
            if (sha1_32(buf) == 249570465) {
                break;
            }
        }
        for (i = -107; i < -95; i++) {
            buf[672] = (byte) i;
            if (sha1_32(buf) == 582136449) {
                break;
            }
        }
        for (i = -119; i < -102; i++) {
            buf[673] = (byte) i;
            if (sha1_32(buf) == -1938402934) {
                break;
            }
        }
        for (i = 102; i < 116; i++) {
            buf[674] = (byte) i;
            if (sha1_32(buf) == -2080802461) {
                break;
            }
        }
        for (i = -85; i < -71; i++) {
            buf[675] = (byte) i;
            if (sha1_32(buf) == 1215792761) {
                break;
            }
        }
        for (i = 60; i < 80; i++) {
            buf[676] = (byte) i;
            if (sha1_32(buf) == 1732904315) {
                break;
            }
        }
        for (i = 47; i < 66; i++) {
            buf[677] = (byte) i;
            if (sha1_32(buf) == -1831694811) {
                break;
            }
        }
        for (i = 56; i < 86; i++) {
            buf[678] = (byte) i;
            if (sha1_32(buf) == -1810599116) {
                break;
            }
        }
        for (i = 80; i < 84; i++) {
            buf[679] = (byte) i;
            if (sha1_32(buf) == 1578216300) {
                break;
            }
        }
        for (i = 76; i < 89; i++) {
            buf[680] = (byte) i;
            if (sha1_32(buf) == 1459969625) {
                break;
            }
        }
        for (i = -97; i < -87; i++) {
            buf[681] = (byte) i;
            if (sha1_32(buf) == -2106045326) {
                break;
            }
        }
        for (i = -42; i < -27; i++) {
            buf[682] = (byte) i;
            if (sha1_32(buf) == -122097769) {
                break;
            }
        }
        for (i = -81; i < -70; i++) {
            buf[683] = (byte) i;
            if (sha1_32(buf) == -1499510109) {
                break;
            }
        }
        for (i = 110; i < 128; i++) {
            buf[684] = (byte) i;
            if (sha1_32(buf) == 1031166732) {
                break;
            }
        }
        for (i = 122; i < 127; i++) {
            buf[685] = (byte) i;
            if (sha1_32(buf) == -635073691) {
                break;
            }
        }
        for (i = -76; i < -65; i++) {
            buf[686] = (byte) i;
            if (sha1_32(buf) == 564201083) {
                break;
            }
        }
        for (i = -69; i < -64; i++) {
            buf[687] = (byte) i;
            if (sha1_32(buf) == 1551100565) {
                break;
            }
        }
        for (i = 32; i < 44; i++) {
            buf[688] = (byte) i;
            if (sha1_32(buf) == 389527208) {
                break;
            }
        }
        for (i = -53; i < -36; i++) {
            buf[689] = (byte) i;
            if (sha1_32(buf) == 1840653067) {
                break;
            }
        }
        for (i = 48; i < 60; i++) {
            buf[690] = (byte) i;
            if (sha1_32(buf) == 113247135) {
                break;
            }
        }
        for (i = -94; i < -83; i++) {
            buf[691] = (byte) i;
            if (sha1_32(buf) == 278128933) {
                break;
            }
        }
        for (i = -118; i < -103; i++) {
            buf[692] = (byte) i;
            if (sha1_32(buf) == -2082531551) {
                break;
            }
        }
        for (i = -12; i < 5; i++) {
            buf[693] = (byte) i;
            if (sha1_32(buf) == -2128396038) {
                break;
            }
        }
        for (i = -60; i < -44; i++) {
            buf[694] = (byte) i;
            if (sha1_32(buf) == 784784337) {
                break;
            }
        }
        for (i = 80; i < 92; i++) {
            buf[695] = (byte) i;
            if (sha1_32(buf) == 596010887) {
                break;
            }
        }
        for (i = 96; i < 112; i++) {
            buf[696] = (byte) i;
            if (sha1_32(buf) == 948103134) {
                break;
            }
        }
        for (i = 60; i < 79; i++) {
            buf[697] = (byte) i;
            if (sha1_32(buf) == 1644587717) {
                break;
            }
        }
        for (i = -39; i < -19; i++) {
            buf[698] = (byte) i;
            if (sha1_32(buf) == -420120334) {
                break;
            }
        }
        for (i = 65; i < 76; i++) {
            buf[699] = (byte) i;
            if (sha1_32(buf) == -634585945) {
                break;
            }
        }
        for (i = -24; i < -1; i++) {
            buf[700] = (byte) i;
            if (sha1_32(buf) == 1172481515) {
                break;
            }
        }
        for (i = -49; i < -28; i++) {
            buf[701] = (byte) i;
            if (sha1_32(buf) == 419482282) {
                break;
            }
        }
        for (i = -47; i < -19; i++) {
            buf[702] = (byte) i;
            if (sha1_32(buf) == 702912229) {
                break;
            }
        }
        for (i = 101; i < 125; i++) {
            buf[703] = (byte) i;
            if (sha1_32(buf) == -545301578) {
                break;
            }
        }
        for (i = 54; i < 78; i++) {
            buf[704] = (byte) i;
            if (sha1_32(buf) == -1076785324) {
                break;
            }
        }
        for (i = 72; i < 87; i++) {
            buf[705] = (byte) i;
            if (sha1_32(buf) == 212352869) {
                break;
            }
        }
        for (i = -126; i < -104; i++) {
            buf[706] = (byte) i;
            if (sha1_32(buf) == 407040560) {
                break;
            }
        }
        for (i = -49; i < -46; i++) {
            buf[707] = (byte) i;
            if (sha1_32(buf) == 954207314) {
                break;
            }
        }
        for (i = -77; i < -69; i++) {
            buf[708] = (byte) i;
            if (sha1_32(buf) == -62846857) {
                break;
            }
        }
        for (i = -113; i < -108; i++) {
            buf[709] = (byte) i;
            if (sha1_32(buf) == -1398080577) {
                break;
            }
        }
        for (i = -5; i < 26; i++) {
            buf[710] = (byte) i;
            if (sha1_32(buf) == 1219039221) {
                break;
            }
        }
        for (i = 90; i < 108; i++) {
            buf[711] = (byte) i;
            if (sha1_32(buf) == -1793360915) {
                break;
            }
        }
        for (i = -81; i < -51; i++) {
            buf[712] = (byte) i;
            if (sha1_32(buf) == -1738027646) {
                break;
            }
        }
        for (i = -120; i < -117; i++) {
            buf[713] = (byte) i;
            if (sha1_32(buf) == 1019380631) {
                break;
            }
        }
        for (i = -99; i < -80; i++) {
            buf[714] = (byte) i;
            if (sha1_32(buf) == -903850138) {
                break;
            }
        }
        for (i = 105; i < 123; i++) {
            buf[715] = (byte) i;
            if (sha1_32(buf) == -1861980645) {
                break;
            }
        }
        for (i = 72; i < 81; i++) {
            buf[716] = (byte) i;
            if (sha1_32(buf) == 2093033047) {
                break;
            }
        }
        for (i = 40; i < 50; i++) {
            buf[717] = (byte) i;
            if (sha1_32(buf) == -1639741040) {
                break;
            }
        }
        for (i = 30; i < 36; i++) {
            buf[718] = (byte) i;
            if (sha1_32(buf) == 1160838845) {
                break;
            }
        }
        for (i = 75; i < 85; i++) {
            buf[719] = (byte) i;
            if (sha1_32(buf) == 960419194) {
                break;
            }
        }
        for (i = 64; i < 87; i++) {
            buf[720] = (byte) i;
            if (sha1_32(buf) == 518332786) {
                break;
            }
        }
        for (i = -104; i < -87; i++) {
            buf[721] = (byte) i;
            if (sha1_32(buf) == -1260439180) {
                break;
            }
        }
        for (i = -89; i < -73; i++) {
            buf[722] = (byte) i;
            if (sha1_32(buf) == 421000791) {
                break;
            }
        }
        for (i = 56; i < 70; i++) {
            buf[723] = (byte) i;
            if (sha1_32(buf) == -1580345000) {
                break;
            }
        }
        for (i = 89; i < 103; i++) {
            buf[724] = (byte) i;
            if (sha1_32(buf) == 1822907701) {
                break;
            }
        }
        for (i = -96; i < -78; i++) {
            buf[725] = (byte) i;
            if (sha1_32(buf) == -433036743) {
                break;
            }
        }
        for (i = -27; i < -22; i++) {
            buf[726] = (byte) i;
            if (sha1_32(buf) == -1156814877) {
                break;
            }
        }
        for (i = -18; i < 4; i++) {
            buf[727] = (byte) i;
            if (sha1_32(buf) == 2027331432) {
                break;
            }
        }
        for (i = 33; i < 54; i++) {
            buf[728] = (byte) i;
            if (sha1_32(buf) == -1283760023) {
                break;
            }
        }
        for (i = -83; i < -63; i++) {
            buf[729] = (byte) i;
            if (sha1_32(buf) == -1674191140) {
                break;
            }
        }
        for (i = -106; i < -91; i++) {
            buf[730] = (byte) i;
            if (sha1_32(buf) == -2144185299) {
                break;
            }
        }
        for (i = 95; i < 105; i++) {
            buf[731] = (byte) i;
            if (sha1_32(buf) == 1727084424) {
                break;
            }
        }
        for (i = -14; i < 3; i++) {
            buf[732] = (byte) i;
            if (sha1_32(buf) == 1870206025) {
                break;
            }
        }
        for (i = -46; i < -30; i++) {
            buf[733] = (byte) i;
            if (sha1_32(buf) == -1717069185) {
                break;
            }
        }
        for (i = -118; i < -100; i++) {
            buf[734] = (byte) i;
            if (sha1_32(buf) == 693554104) {
                break;
            }
        }
        for (i = -22; i < -11; i++) {
            buf[735] = (byte) i;
            if (sha1_32(buf) == -1279480032) {
                break;
            }
        }
        for (i = 42; i < 64; i++) {
            buf[736] = (byte) i;
            if (sha1_32(buf) == -12653429) {
                break;
            }
        }
        for (i = -28; i < -19; i++) {
            buf[737] = (byte) i;
            if (sha1_32(buf) == 686756489) {
                break;
            }
        }
        for (i = -36; i < -19; i++) {
            buf[738] = (byte) i;
            if (sha1_32(buf) == 1462640194) {
                break;
            }
        }
        for (i = -70; i < -66; i++) {
            buf[739] = (byte) i;
            if (sha1_32(buf) == 1249400226) {
                break;
            }
        }
        for (i = -72; i < -60; i++) {
            buf[740] = (byte) i;
            if (sha1_32(buf) == -412669072) {
                break;
            }
        }
        for (i = -35; i < -26; i++) {
            buf[741] = (byte) i;
            if (sha1_32(buf) == -1891617659) {
                break;
            }
        }
        for (i = 113; i < 128; i++) {
            buf[742] = (byte) i;
            if (sha1_32(buf) == 259384733) {
                break;
            }
        }
        for (i = -48; i < -20; i++) {
            buf[743] = (byte) i;
            if (sha1_32(buf) == 1037339533) {
                break;
            }
        }
        for (i = 10; i < 19; i++) {
            buf[744] = (byte) i;
            if (sha1_32(buf) == 5189993) {
                break;
            }
        }
        for (i = -52; i < -51; i++) {
            buf[745] = (byte) i;
            if (sha1_32(buf) == 1777468953) {
                break;
            }
        }
        for (i = -39; i < -27; i++) {
            buf[746] = (byte) i;
            if (sha1_32(buf) == -395345591) {
                break;
            }
        }
        for (i = -105; i < -76; i++) {
            buf[747] = (byte) i;
            if (sha1_32(buf) == -116450518) {
                break;
            }
        }
        for (i = -121; i < -101; i++) {
            buf[748] = (byte) i;
            if (sha1_32(buf) == -1390347877) {
                break;
            }
        }
        for (i = 28; i < 36; i++) {
            buf[749] = (byte) i;
            if (sha1_32(buf) == 1240046199) {
                break;
            }
        }
        for (i = -71; i < -56; i++) {
            buf[750] = (byte) i;
            if (sha1_32(buf) == 1750639790) {
                break;
            }
        }
        for (i = 98; i < 108; i++) {
            buf[751] = (byte) i;
            if (sha1_32(buf) == 1406419852) {
                break;
            }
        }
        for (i = -91; i < -75; i++) {
            buf[752] = (byte) i;
            if (sha1_32(buf) == -1633183795) {
                break;
            }
        }
        for (i = -96; i < -83; i++) {
            buf[753] = (byte) i;
            if (sha1_32(buf) == 752082861) {
                break;
            }
        }
        for (i = -17; i < 6; i++) {
            buf[754] = (byte) i;
            if (sha1_32(buf) == 962052913) {
                break;
            }
        }
        for (i = -62; i < -56; i++) {
            buf[755] = (byte) i;
            if (sha1_32(buf) == 1968065673) {
                break;
            }
        }
        for (i = 38; i < 63; i++) {
            buf[756] = (byte) i;
            if (sha1_32(buf) == 413857131) {
                break;
            }
        }
        for (i = 15; i < 35; i++) {
            buf[757] = (byte) i;
            if (sha1_32(buf) == 864990791) {
                break;
            }
        }
        for (i = 109; i < 127; i++) {
            buf[758] = (byte) i;
            if (sha1_32(buf) == 344183571) {
                break;
            }
        }
        for (i = 30; i < 39; i++) {
            buf[759] = (byte) i;
            if (sha1_32(buf) == -255357654) {
                break;
            }
        }
        for (i = -11; i < -3; i++) {
            buf[760] = (byte) i;
            if (sha1_32(buf) == 2032776199) {
                break;
            }
        }
        for (i = -90; i < -65; i++) {
            buf[761] = (byte) i;
            if (sha1_32(buf) == -577857993) {
                break;
            }
        }
        for (i = -41; i < -22; i++) {
            buf[762] = (byte) i;
            if (sha1_32(buf) == 764118661) {
                break;
            }
        }
        for (i = 121; i < 128; i++) {
            buf[763] = (byte) i;
            if (sha1_32(buf) == 739418832) {
                break;
            }
        }
        for (i = -111; i < -82; i++) {
            buf[764] = (byte) i;
            if (sha1_32(buf) == 1584502491) {
                break;
            }
        }
        for (i = 63; i < 82; i++) {
            buf[765] = (byte) i;
            if (sha1_32(buf) == 1065122260) {
                break;
            }
        }
        for (i = 117; i < 128; i++) {
            buf[766] = (byte) i;
            if (sha1_32(buf) == 593601850) {
                break;
            }
        }
        for (i = -118; i < -106; i++) {
            buf[767] = (byte) i;
            if (sha1_32(buf) == 2299231) {
                break;
            }
        }
        for (i = 46; i < 56; i++) {
            buf[768] = (byte) i;
            if (sha1_32(buf) == -944872615) {
                break;
            }
        }
        for (i = -126; i < -113; i++) {
            buf[769] = (byte) i;
            if (sha1_32(buf) == -1340152917) {
                break;
            }
        }
        for (i = -47; i < -38; i++) {
            buf[770] = (byte) i;
            if (sha1_32(buf) == 608867129) {
                break;
            }
        }
        for (i = -38; i < -14; i++) {
            buf[771] = (byte) i;
            if (sha1_32(buf) == 1280253965) {
                break;
            }
        }
        for (i = 97; i < 118; i++) {
            buf[772] = (byte) i;
            if (sha1_32(buf) == -1968881361) {
                break;
            }
        }
        for (i = -40; i < -12; i++) {
            buf[773] = (byte) i;
            if (sha1_32(buf) == 414360682) {
                break;
            }
        }
        for (i = 68; i < 73; i++) {
            buf[774] = (byte) i;
            if (sha1_32(buf) == -675900963) {
                break;
            }
        }
        for (i = 25; i < 42; i++) {
            buf[775] = (byte) i;
            if (sha1_32(buf) == -2053926584) {
                break;
            }
        }
        for (i = 86; i < 109; i++) {
            buf[776] = (byte) i;
            if (sha1_32(buf) == 2038396354) {
                break;
            }
        }
        for (i = 59; i < 66; i++) {
            buf[777] = (byte) i;
            if (sha1_32(buf) == 169565511) {
                break;
            }
        }
        for (i = -128; i < -106; i++) {
            buf[778] = (byte) i;
            if (sha1_32(buf) == -1610752750) {
                break;
            }
        }
        for (i = -70; i < -56; i++) {
            buf[779] = (byte) i;
            if (sha1_32(buf) == 1706705632) {
                break;
            }
        }
        for (i = -21; i < -5; i++) {
            buf[780] = (byte) i;
            if (sha1_32(buf) == 157910832) {
                break;
            }
        }
        for (i = 21; i < 34; i++) {
            buf[781] = (byte) i;
            if (sha1_32(buf) == -422606244) {
                break;
            }
        }
        for (i = -2; i < 13; i++) {
            buf[782] = (byte) i;
            if (sha1_32(buf) == -750327383) {
                break;
            }
        }
        for (i = 99; i < 118; i++) {
            buf[783] = (byte) i;
            if (sha1_32(buf) == 723646629) {
                break;
            }
        }
        for (i = -8; i < 16; i++) {
            buf[784] = (byte) i;
            if (sha1_32(buf) == -1117856362) {
                break;
            }
        }
        for (i = 20; i < 47; i++) {
            buf[785] = (byte) i;
            if (sha1_32(buf) == 2105894231) {
                break;
            }
        }
        for (i = 49; i < 70; i++) {
            buf[786] = (byte) i;
            if (sha1_32(buf) == 1527393591) {
                break;
            }
        }
        for (i = -73; i < -69; i++) {
            buf[787] = (byte) i;
            if (sha1_32(buf) == -1265198724) {
                break;
            }
        }
        for (i = -99; i < -78; i++) {
            buf[788] = (byte) i;
            if (sha1_32(buf) == 2070354419) {
                break;
            }
        }
        for (i = 12; i < 22; i++) {
            buf[789] = (byte) i;
            if (sha1_32(buf) == 1372860761) {
                break;
            }
        }
        for (i = 59; i < 68; i++) {
            buf[790] = (byte) i;
            if (sha1_32(buf) == -265333366) {
                break;
            }
        }
        for (i = 50; i < 74; i++) {
            buf[791] = (byte) i;
            if (sha1_32(buf) == 63668321) {
                break;
            }
        }
        for (i = 38; i < 54; i++) {
            buf[792] = (byte) i;
            if (sha1_32(buf) == -1826283896) {
                break;
            }
        }
        for (i = -106; i < -100; i++) {
            buf[793] = (byte) i;
            if (sha1_32(buf) == 1369215966) {
                break;
            }
        }
        for (i = 91; i < 108; i++) {
            buf[794] = (byte) i;
            if (sha1_32(buf) == -2034982884) {
                break;
            }
        }
        for (i = 93; i < 116; i++) {
            buf[795] = (byte) i;
            if (sha1_32(buf) == 985918661) {
                break;
            }
        }
        for (i = -99; i < -87; i++) {
            buf[796] = (byte) i;
            if (sha1_32(buf) == -548194990) {
                break;
            }
        }
        for (i = 124; i < 128; i++) {
            buf[797] = (byte) i;
            if (sha1_32(buf) == -36133412) {
                break;
            }
        }
        for (i = 101; i < 121; i++) {
            buf[798] = (byte) i;
            if (sha1_32(buf) == -1431343590) {
                break;
            }
        }
        for (i = 75; i < 87; i++) {
            buf[799] = (byte) i;
            if (sha1_32(buf) == 970602373) {
                break;
            }
        }
        for (i = 7; i < 18; i++) {
            buf[800] = (byte) i;
            if (sha1_32(buf) == -1681447909) {
                break;
            }
        }
        for (i = 110; i < 128; i++) {
            buf[801] = (byte) i;
            if (sha1_32(buf) == -424494579) {
                break;
            }
        }
        for (i = -50; i < -32; i++) {
            buf[802] = (byte) i;
            if (sha1_32(buf) == -467156361) {
                break;
            }
        }
        for (i = -104; i < -82; i++) {
            buf[803] = (byte) i;
            if (sha1_32(buf) == 228823606) {
                break;
            }
        }
        for (i = -6; i < 20; i++) {
            buf[804] = (byte) i;
            if (sha1_32(buf) == -404765998) {
                break;
            }
        }
        for (i = -122; i < -105; i++) {
            buf[805] = (byte) i;
            if (sha1_32(buf) == -954608728) {
                break;
            }
        }
        for (i = 19; i < 39; i++) {
            buf[806] = (byte) i;
            if (sha1_32(buf) == -274177905) {
                break;
            }
        }
        for (i = 25; i < 52; i++) {
            buf[807] = (byte) i;
            if (sha1_32(buf) == 1025773701) {
                break;
            }
        }
        for (i = 34; i < 48; i++) {
            buf[808] = (byte) i;
            if (sha1_32(buf) == 834202075) {
                break;
            }
        }
        for (i = -40; i < -23; i++) {
            buf[809] = (byte) i;
            if (sha1_32(buf) == -884977372) {
                break;
            }
        }
        for (i = -114; i < -107; i++) {
            buf[810] = (byte) i;
            if (sha1_32(buf) == -431189382) {
                break;
            }
        }
        for (i = 41; i < 64; i++) {
            buf[811] = (byte) i;
            if (sha1_32(buf) == 1764564773) {
                break;
            }
        }
        for (i = 38; i < 46; i++) {
            buf[812] = (byte) i;
            if (sha1_32(buf) == 993319361) {
                break;
            }
        }
        for (i = -121; i < -95; i++) {
            buf[813] = (byte) i;
            if (sha1_32(buf) == 2137733044) {
                break;
            }
        }
        for (i = -11; i < 6; i++) {
            buf[814] = (byte) i;
            if (sha1_32(buf) == -1315891528) {
                break;
            }
        }
        for (i = 28; i < 39; i++) {
            buf[815] = (byte) i;
            if (sha1_32(buf) == -1863366354) {
                break;
            }
        }
        for (i = 105; i < 111; i++) {
            buf[816] = (byte) i;
            if (sha1_32(buf) == -2031075962) {
                break;
            }
        }
        for (i = -75; i < -46; i++) {
            buf[817] = (byte) i;
            if (sha1_32(buf) == 1018013022) {
                break;
            }
        }
        for (i = 106; i < 127; i++) {
            buf[818] = (byte) i;
            if (sha1_32(buf) == -2069868556) {
                break;
            }
        }
        for (i = 68; i < 78; i++) {
            buf[819] = (byte) i;
            if (sha1_32(buf) == -467077847) {
                break;
            }
        }
        for (i = -12; i < -1; i++) {
            buf[820] = (byte) i;
            if (sha1_32(buf) == 232315028) {
                break;
            }
        }
        for (i = -48; i < -32; i++) {
            buf[821] = (byte) i;
            if (sha1_32(buf) == 954090532) {
                break;
            }
        }
        for (i = 25; i < 33; i++) {
            buf[822] = (byte) i;
            if (sha1_32(buf) == -1517433419) {
                break;
            }
        }
        for (i = 58; i < 74; i++) {
            buf[823] = (byte) i;
            if (sha1_32(buf) == 443361863) {
                break;
            }
        }
        for (i = 25; i < 29; i++) {
            buf[824] = (byte) i;
            if (sha1_32(buf) == -518853680) {
                break;
            }
        }
        for (i = 58; i < 80; i++) {
            buf[825] = (byte) i;
            if (sha1_32(buf) == 1540140905) {
                break;
            }
        }
        for (i = 36; i < 57; i++) {
            buf[826] = (byte) i;
            if (sha1_32(buf) == -1020196015) {
                break;
            }
        }
        for (i = 17; i < 38; i++) {
            buf[827] = (byte) i;
            if (sha1_32(buf) == 1086030210) {
                break;
            }
        }
        for (i = -2; i < 29; i++) {
            buf[828] = (byte) i;
            if (sha1_32(buf) == -930085810) {
                break;
            }
        }
        for (i = 96; i < 104; i++) {
            buf[829] = (byte) i;
            if (sha1_32(buf) == 3154968) {
                break;
            }
        }
        for (i = 73; i < 101; i++) {
            buf[830] = (byte) i;
            if (sha1_32(buf) == 461643313) {
                break;
            }
        }
        for (i = -73; i < -53; i++) {
            buf[831] = (byte) i;
            if (sha1_32(buf) == 362308356) {
                break;
            }
        }
        for (i = 63; i < 75; i++) {
            buf[832] = (byte) i;
            if (sha1_32(buf) == -1550309827) {
                break;
            }
        }
        for (i = 14; i < 34; i++) {
            buf[833] = (byte) i;
            if (sha1_32(buf) == -192162334) {
                break;
            }
        }
        for (i = -53; i < -38; i++) {
            buf[834] = (byte) i;
            if (sha1_32(buf) == 698021824) {
                break;
            }
        }
        for (i = -8; i < 10; i++) {
            buf[835] = (byte) i;
            if (sha1_32(buf) == 698021824) {
                break;
            }
        }
        for (i = -117; i < -105; i++) {
            buf[836] = (byte) i;
            if (sha1_32(buf) == -1855555090) {
                break;
            }
        }
        for (i = -73; i < -67; i++) {
            buf[837] = (byte) i;
            if (sha1_32(buf) == -1867143868) {
                break;
            }
        }
        for (i = -48; i < -34; i++) {
            buf[838] = (byte) i;
            if (sha1_32(buf) == 1045172103) {
                break;
            }
        }
        for (i = 37; i < 50; i++) {
            buf[839] = (byte) i;
            if (sha1_32(buf) == -1495078452) {
                break;
            }
        }
        for (i = 87; i < 105; i++) {
            buf[840] = (byte) i;
            if (sha1_32(buf) == -951219833) {
                break;
            }
        }
        for (i = 26; i < 39; i++) {
            buf[841] = (byte) i;
            if (sha1_32(buf) == -1752637401) {
                break;
            }
        }
        for (i = 58; i < 77; i++) {
            buf[842] = (byte) i;
            if (sha1_32(buf) == 1474532988) {
                break;
            }
        }
        for (i = 65; i < 86; i++) {
            buf[843] = (byte) i;
            if (sha1_32(buf) == -1482471227) {
                break;
            }
        }
        for (i = -101; i < -90; i++) {
            buf[844] = (byte) i;
            if (sha1_32(buf) == -2006479514) {
                break;
            }
        }
        for (i = 82; i < 101; i++) {
            buf[845] = (byte) i;
            if (sha1_32(buf) == -132839672) {
                break;
            }
        }
        for (i = -45; i < -42; i++) {
            buf[846] = (byte) i;
            if (sha1_32(buf) == -775072643) {
                break;
            }
        }
        for (i = 120; i < 128; i++) {
            buf[847] = (byte) i;
            if (sha1_32(buf) == -1641532010) {
                break;
            }
        }
        for (i = -115; i < -93; i++) {
            buf[848] = (byte) i;
            if (sha1_32(buf) == 935535943) {
                break;
            }
        }
        for (i = -2; i < 3; i++) {
            buf[849] = (byte) i;
            if (sha1_32(buf) == 945645901) {
                break;
            }
        }
        for (i = -17; i < 2; i++) {
            buf[850] = (byte) i;
            if (sha1_32(buf) == 2050989449) {
                break;
            }
        }
        for (i = 97; i < 107; i++) {
            buf[851] = (byte) i;
            if (sha1_32(buf) == 228230737) {
                break;
            }
        }
        for (i = -70; i < -51; i++) {
            buf[852] = (byte) i;
            if (sha1_32(buf) == 875382782) {
                break;
            }
        }
        for (i = 31; i < 51; i++) {
            buf[853] = (byte) i;
            if (sha1_32(buf) == 1516340547) {
                break;
            }
        }
        for (i = 78; i < 96; i++) {
            buf[854] = (byte) i;
            if (sha1_32(buf) == -1915098541) {
                break;
            }
        }
        for (i = 62; i < 75; i++) {
            buf[855] = (byte) i;
            if (sha1_32(buf) == -1938364655) {
                break;
            }
        }
        for (i = 66; i < 85; i++) {
            buf[856] = (byte) i;
            if (sha1_32(buf) == -917590238) {
                break;
            }
        }
        for (i = -36; i < -31; i++) {
            buf[857] = (byte) i;
            if (sha1_32(buf) == -1668255714) {
                break;
            }
        }
        for (i = 0; i < 17; i++) {
            buf[858] = (byte) i;
            if (sha1_32(buf) == 1241864995) {
                break;
            }
        }
        for (i = -127; i < -109; i++) {
            buf[859] = (byte) i;
            if (sha1_32(buf) == -1389425133) {
                break;
            }
        }
        for (i = 67; i < 83; i++) {
            buf[860] = (byte) i;
            if (sha1_32(buf) == 213200578) {
                break;
            }
        }
        for (i = 81; i < 95; i++) {
            buf[861] = (byte) i;
            if (sha1_32(buf) == -1920121661) {
                break;
            }
        }
        for (i = 84; i < 110; i++) {
            buf[862] = (byte) i;
            if (sha1_32(buf) == 641477941) {
                break;
            }
        }
        for (i = 58; i < 70; i++) {
            buf[863] = (byte) i;
            if (sha1_32(buf) == -2082789203) {
                break;
            }
        }
        for (i = -120; i < -98; i++) {
            buf[864] = (byte) i;
            if (sha1_32(buf) == 1441491804) {
                break;
            }
        }
        for (i = -111; i < -90; i++) {
            buf[865] = (byte) i;
            if (sha1_32(buf) == 1530225193) {
                break;
            }
        }
        for (i = 65; i < 77; i++) {
            buf[866] = (byte) i;
            if (sha1_32(buf) == -407937242) {
                break;
            }
        }
        for (i = 99; i < 113; i++) {
            buf[867] = (byte) i;
            if (sha1_32(buf) == 1368434793) {
                break;
            }
        }
        for (i = -81; i < -64; i++) {
            buf[868] = (byte) i;
            if (sha1_32(buf) == 328463083) {
                break;
            }
        }
        for (i = 109; i < 124; i++) {
            buf[869] = (byte) i;
            if (sha1_32(buf) == 1147726303) {
                break;
            }
        }
        for (i = -47; i < -37; i++) {
            buf[870] = (byte) i;
            if (sha1_32(buf) == 2019786902) {
                break;
            }
        }
        for (i = -128; i < -111; i++) {
            buf[871] = (byte) i;
            if (sha1_32(buf) == -1240432757) {
                break;
            }
        }
        for (i = -26; i < -3; i++) {
            buf[872] = (byte) i;
            if (sha1_32(buf) == 1984497976) {
                break;
            }
        }
        for (i = -26; i < -12; i++) {
            buf[873] = (byte) i;
            if (sha1_32(buf) == -2064335040) {
                break;
            }
        }
        for (i = 50; i < 61; i++) {
            buf[874] = (byte) i;
            if (sha1_32(buf) == 1468573722) {
                break;
            }
        }
        for (i = 77; i < 96; i++) {
            buf[875] = (byte) i;
            if (sha1_32(buf) == 719736184) {
                break;
            }
        }
        for (i = -5; i < 6; i++) {
            buf[876] = (byte) i;
            if (sha1_32(buf) == 384086279) {
                break;
            }
        }
        for (i = 12; i < 31; i++) {
            buf[877] = (byte) i;
            if (sha1_32(buf) == -1724896021) {
                break;
            }
        }
        for (i = -78; i < -62; i++) {
            buf[878] = (byte) i;
            if (sha1_32(buf) == -754765802) {
                break;
            }
        }
        for (i = 7; i < 13; i++) {
            buf[879] = (byte) i;
            if (sha1_32(buf) == -182275488) {
                break;
            }
        }
        for (i = -4; i < 0; i++) {
            buf[880] = (byte) i;
            if (sha1_32(buf) == -1581303236) {
                break;
            }
        }
        for (i = 85; i < 107; i++) {
            buf[881] = (byte) i;
            if (sha1_32(buf) == -415352107) {
                break;
            }
        }
        for (i = -90; i < -73; i++) {
            buf[882] = (byte) i;
            if (sha1_32(buf) == 829502822) {
                break;
            }
        }
        for (i = -75; i < -65; i++) {
            buf[883] = (byte) i;
            if (sha1_32(buf) == -661909348) {
                break;
            }
        }
        for (i = 92; i < 112; i++) {
            buf[884] = (byte) i;
            if (sha1_32(buf) == 466608850) {
                break;
            }
        }
        for (i = -48; i < -33; i++) {
            buf[885] = (byte) i;
            if (sha1_32(buf) == 1387504439) {
                break;
            }
        }
        for (i = 92; i < 113; i++) {
            buf[886] = (byte) i;
            if (sha1_32(buf) == -2092896072) {
                break;
            }
        }
        for (i = 55; i < 84; i++) {
            buf[887] = (byte) i;
            if (sha1_32(buf) == -1014606553) {
                break;
            }
        }
        for (i = 71; i < 81; i++) {
            buf[888] = (byte) i;
            if (sha1_32(buf) == 860719032) {
                break;
            }
        }
        for (i = 0; i < 13; i++) {
            buf[889] = (byte) i;
            if (sha1_32(buf) == -1050474561) {
                break;
            }
        }
        for (i = -53; i < -24; i++) {
            buf[890] = (byte) i;
            if (sha1_32(buf) == 86890178) {
                break;
            }
        }
        for (i = 73; i < 84; i++) {
            buf[891] = (byte) i;
            if (sha1_32(buf) == 1051300937) {
                break;
            }
        }
        for (i = -44; i < -28; i++) {
            buf[892] = (byte) i;
            if (sha1_32(buf) == 1581239864) {
                break;
            }
        }
        for (i = -101; i < -86; i++) {
            buf[893] = (byte) i;
            if (sha1_32(buf) == -339062758) {
                break;
            }
        }
        for (i = -128; i < -125; i++) {
            buf[894] = (byte) i;
            if (sha1_32(buf) == -57642497) {
                break;
            }
        }
        for (i = -128; i < -127; i++) {
            buf[895] = (byte) i;
            if (sha1_32(buf) == 1459863742) {
                break;
            }
        }
        for (i = -90; i < -76; i++) {
            buf[896] = (byte) i;
            if (sha1_32(buf) == 2111287253) {
                break;
            }
        }
        for (i = -27; i < -14; i++) {
            buf[897] = (byte) i;
            if (sha1_32(buf) == -1219417236) {
                break;
            }
        }
        for (i = -44; i < -22; i++) {
            buf[898] = (byte) i;
            if (sha1_32(buf) == 1138057314) {
                break;
            }
        }
        for (i = -51; i < -46; i++) {
            buf[899] = (byte) i;
            if (sha1_32(buf) == -663263564) {
                break;
            }
        }
        for (i = -95; i < -70; i++) {
            buf[900] = (byte) i;
            if (sha1_32(buf) == 1643537828) {
                break;
            }
        }
        for (i = 8; i < 34; i++) {
            buf[901] = (byte) i;
            if (sha1_32(buf) == -1573470265) {
                break;
            }
        }
        for (i = 2; i < 3; i++) {
            buf[902] = (byte) i;
            if (sha1_32(buf) == 240830478) {
                break;
            }
        }
        for (i = -6; i < -3; i++) {
            buf[903] = (byte) i;
            if (sha1_32(buf) == -1884841846) {
                break;
            }
        }
        for (i = -5; i < 1; i++) {
            buf[904] = (byte) i;
            if (sha1_32(buf) == -491032274) {
                break;
            }
        }
        for (i = -11; i < 10; i++) {
            buf[905] = (byte) i;
            if (sha1_32(buf) == 213910954) {
                break;
            }
        }
        for (i = -96; i < -79; i++) {
            buf[906] = (byte) i;
            if (sha1_32(buf) == -320012258) {
                break;
            }
        }
        for (i = 17; i < 40; i++) {
            buf[907] = (byte) i;
            if (sha1_32(buf) == 951237242) {
                break;
            }
        }
        for (i = 30; i < 45; i++) {
            buf[908] = (byte) i;
            if (sha1_32(buf) == -780934206) {
                break;
            }
        }
        for (i = -96; i < -87; i++) {
            buf[909] = (byte) i;
            if (sha1_32(buf) == -262037445) {
                break;
            }
        }
        for (i = -11; i < 1; i++) {
            buf[910] = (byte) i;
            if (sha1_32(buf) == 952903288) {
                break;
            }
        }
        for (i = 98; i < 121; i++) {
            buf[911] = (byte) i;
            if (sha1_32(buf) == 1449934535) {
                break;
            }
        }
        for (i = 56; i < 69; i++) {
            buf[912] = (byte) i;
            if (sha1_32(buf) == -2079858668) {
                break;
            }
        }
        for (i = -95; i < -69; i++) {
            buf[913] = (byte) i;
            if (sha1_32(buf) == 1570786132) {
                break;
            }
        }
        for (i = -20; i < 5; i++) {
            buf[914] = (byte) i;
            if (sha1_32(buf) == -1114365077) {
                break;
            }
        }
        for (i = -101; i < -83; i++) {
            buf[915] = (byte) i;
            if (sha1_32(buf) == 2062123427) {
                break;
            }
        }
        for (i = 66; i < 75; i++) {
            buf[916] = (byte) i;
            if (sha1_32(buf) == 2000992962) {
                break;
            }
        }
        for (i = 74; i < 87; i++) {
            buf[917] = (byte) i;
            if (sha1_32(buf) == -1529826023) {
                break;
            }
        }
        for (i = 11; i < 28; i++) {
            buf[918] = (byte) i;
            if (sha1_32(buf) == 1048231172) {
                break;
            }
        }
        for (i = 3; i < 18; i++) {
            buf[919] = (byte) i;
            if (sha1_32(buf) == 565866560) {
                break;
            }
        }
        for (i = 22; i < 34; i++) {
            buf[920] = (byte) i;
            if (sha1_32(buf) == -116099856) {
                break;
            }
        }
        for (i = 106; i < 114; i++) {
            buf[921] = (byte) i;
            if (sha1_32(buf) == 2076518765) {
                break;
            }
        }
        for (i = 90; i < 114; i++) {
            buf[922] = (byte) i;
            if (sha1_32(buf) == -1718581170) {
                break;
            }
        }
        for (i = -28; i < -19; i++) {
            buf[923] = (byte) i;
            if (sha1_32(buf) == 198124855) {
                break;
            }
        }
        for (i = 55; i < 62; i++) {
            buf[924] = (byte) i;
            if (sha1_32(buf) == 1601032830) {
                break;
            }
        }
        for (i = 11; i < 26; i++) {
            buf[925] = (byte) i;
            if (sha1_32(buf) == 653079235) {
                break;
            }
        }
        for (i = -57; i < -46; i++) {
            buf[926] = (byte) i;
            if (sha1_32(buf) == 405317672) {
                break;
            }
        }
        for (i = 11; i < 27; i++) {
            buf[927] = (byte) i;
            if (sha1_32(buf) == -928749551) {
                break;
            }
        }
        for (i = 5; i < 20; i++) {
            buf[928] = (byte) i;
            if (sha1_32(buf) == -2103939459) {
                break;
            }
        }
        for (i = -28; i < -10; i++) {
            buf[929] = (byte) i;
            if (sha1_32(buf) == 1395942854) {
                break;
            }
        }
        for (i = 57; i < 81; i++) {
            buf[930] = (byte) i;
            if (sha1_32(buf) == 221180620) {
                break;
            }
        }
        for (i = 34; i < 51; i++) {
            buf[931] = (byte) i;
            if (sha1_32(buf) == 930502171) {
                break;
            }
        }
        for (i = 93; i < 102; i++) {
            buf[932] = (byte) i;
            if (sha1_32(buf) == -1930014874) {
                break;
            }
        }
        for (i = -3; i < 13; i++) {
            buf[933] = (byte) i;
            if (sha1_32(buf) == -1441680272) {
                break;
            }
        }
        for (i = -65; i < -55; i++) {
            buf[934] = (byte) i;
            if (sha1_32(buf) == -103263236) {
                break;
            }
        }
        for (i = 5; i < 28; i++) {
            buf[935] = (byte) i;
            if (sha1_32(buf) == 38882576) {
                break;
            }
        }
        for (i = -62; i < -47; i++) {
            buf[936] = (byte) i;
            if (sha1_32(buf) == 702409545) {
                break;
            }
        }
        for (i = -14; i < -5; i++) {
            buf[937] = (byte) i;
            if (sha1_32(buf) == 565978615) {
                break;
            }
        }
        for (i = -107; i < -90; i++) {
            buf[938] = (byte) i;
            if (sha1_32(buf) == -1616641328) {
                break;
            }
        }
        for (i = 3; i < 8; i++) {
            buf[939] = (byte) i;
            if (sha1_32(buf) == -1217308015) {
                break;
            }
        }
        for (i = -89; i < -72; i++) {
            buf[940] = (byte) i;
            if (sha1_32(buf) == 1966439803) {
                break;
            }
        }
        for (i = 8; i < 25; i++) {
            buf[941] = (byte) i;
            if (sha1_32(buf) == 1681215022) {
                break;
            }
        }
        for (i = -52; i < -37; i++) {
            buf[942] = (byte) i;
            if (sha1_32(buf) == 1307597808) {
                break;
            }
        }
        for (i = 110; i < 127; i++) {
            buf[943] = (byte) i;
            if (sha1_32(buf) == -1683287831) {
                break;
            }
        }
        for (i = -123; i < -105; i++) {
            buf[944] = (byte) i;
            if (sha1_32(buf) == -811632184) {
                break;
            }
        }
        for (i = -111; i < -103; i++) {
            buf[945] = (byte) i;
            if (sha1_32(buf) == 679222704) {
                break;
            }
        }
        for (i = -57; i < -43; i++) {
            buf[946] = (byte) i;
            if (sha1_32(buf) == -184502943) {
                break;
            }
        }
        for (i = -55; i < -41; i++) {
            buf[947] = (byte) i;
            if (sha1_32(buf) == 1348030034) {
                break;
            }
        }
        for (i = -35; i < -11; i++) {
            buf[948] = (byte) i;
            if (sha1_32(buf) == -89392837) {
                break;
            }
        }
        for (i = -42; i < -34; i++) {
            buf[949] = (byte) i;
            if (sha1_32(buf) == -996564351) {
                break;
            }
        }
        for (i = -76; i < -64; i++) {
            buf[950] = (byte) i;
            if (sha1_32(buf) == 393929148) {
                break;
            }
        }
        for (i = -84; i < -67; i++) {
            buf[951] = (byte) i;
            if (sha1_32(buf) == 879919715) {
                break;
            }
        }
        for (i = -78; i < -65; i++) {
            buf[952] = (byte) i;
            if (sha1_32(buf) == -615748881) {
                break;
            }
        }
        for (i = 49; i < 66; i++) {
            buf[953] = (byte) i;
            if (sha1_32(buf) == 2000365422) {
                break;
            }
        }
        for (i = 55; i < 66; i++) {
            buf[954] = (byte) i;
            if (sha1_32(buf) == 1943712063) {
                break;
            }
        }
        for (i = 31; i < 40; i++) {
            buf[955] = (byte) i;
            if (sha1_32(buf) == 1867075002) {
                break;
            }
        }
        for (i = -105; i < -84; i++) {
            buf[956] = (byte) i;
            if (sha1_32(buf) == 507048716) {
                break;
            }
        }
        for (i = -108; i < -93; i++) {
            buf[957] = (byte) i;
            if (sha1_32(buf) == 128995879) {
                break;
            }
        }
        for (i = 89; i < 110; i++) {
            buf[958] = (byte) i;
            if (sha1_32(buf) == 1589501375) {
                break;
            }
        }
        for (i = 62; i < 88; i++) {
            buf[959] = (byte) i;
            if (sha1_32(buf) == -1172272989) {
                break;
            }
        }
        for (i = 7; i < 19; i++) {
            buf[960] = (byte) i;
            if (sha1_32(buf) == 531808810) {
                break;
            }
        }
        for (i = 37; i < 51; i++) {
            buf[961] = (byte) i;
            if (sha1_32(buf) == -598245177) {
                break;
            }
        }
        for (i = -15; i < 0; i++) {
            buf[962] = (byte) i;
            if (sha1_32(buf) == -76855567) {
                break;
            }
        }
        for (i = 73; i < 78; i++) {
            buf[963] = (byte) i;
            if (sha1_32(buf) == -1412893941) {
                break;
            }
        }
        for (i = 93; i < 116; i++) {
            buf[964] = (byte) i;
            if (sha1_32(buf) == 377841234) {
                break;
            }
        }
        for (i = 125; i < 128; i++) {
            buf[965] = (byte) i;
            if (sha1_32(buf) == -1091184229) {
                break;
            }
        }
        for (i = -77; i < -49; i++) {
            buf[966] = (byte) i;
            if (sha1_32(buf) == 1437302849) {
                break;
            }
        }
        for (i = -122; i < -115; i++) {
            buf[967] = (byte) i;
            if (sha1_32(buf) == 705098178) {
                break;
            }
        }
        for (i = -68; i < -61; i++) {
            buf[968] = (byte) i;
            if (sha1_32(buf) == 932705910) {
                break;
            }
        }
        for (i = -66; i < -40; i++) {
            buf[969] = (byte) i;
            if (sha1_32(buf) == 1352885045) {
                break;
            }
        }
        for (i = -124; i < -107; i++) {
            buf[970] = (byte) i;
            if (sha1_32(buf) == 1998265447) {
                break;
            }
        }
        for (i = -75; i < -74; i++) {
            buf[971] = (byte) i;
            if (sha1_32(buf) == -1003491487) {
                break;
            }
        }
        for (i = 104; i < 123; i++) {
            buf[972] = (byte) i;
            if (sha1_32(buf) == -185687826) {
                break;
            }
        }
        for (i = -8; i < 14; i++) {
            buf[973] = (byte) i;
            if (sha1_32(buf) == -1033952645) {
                break;
            }
        }
        for (i = 62; i < 76; i++) {
            buf[974] = (byte) i;
            if (sha1_32(buf) == -87934377) {
                break;
            }
        }
        for (i = -121; i < -109; i++) {
            buf[975] = (byte) i;
            if (sha1_32(buf) == -784494350) {
                break;
            }
        }
        for (i = -10; i < -6; i++) {
            buf[976] = (byte) i;
            if (sha1_32(buf) == 279024087) {
                break;
            }
        }
        for (i = -115; i < -88; i++) {
            buf[977] = (byte) i;
            if (sha1_32(buf) == 1870220417) {
                break;
            }
        }
        for (i = 101; i < 108; i++) {
            buf[978] = (byte) i;
            if (sha1_32(buf) == 2084231040) {
                break;
            }
        }
        for (i = 22; i < 33; i++) {
            buf[979] = (byte) i;
            if (sha1_32(buf) == -656256599) {
                break;
            }
        }
        for (i = 77; i < 82; i++) {
            buf[980] = (byte) i;
            if (sha1_32(buf) == -717715255) {
                break;
            }
        }
        for (i = -88; i < -76; i++) {
            buf[981] = (byte) i;
            if (sha1_32(buf) == 31097408) {
                break;
            }
        }
        for (i = 1; i < 24; i++) {
            buf[982] = (byte) i;
            if (sha1_32(buf) == -1683673069) {
                break;
            }
        }
        for (i = 56; i < 62; i++) {
            buf[983] = (byte) i;
            if (sha1_32(buf) == -433926369) {
                break;
            }
        }
        for (i = 27; i < 45; i++) {
            buf[984] = (byte) i;
            if (sha1_32(buf) == -216334420) {
                break;
            }
        }
        for (i = 122; i < 125; i++) {
            buf[985] = (byte) i;
            if (sha1_32(buf) == 1188329605) {
                break;
            }
        }
        for (i = 64; i < 69; i++) {
            buf[986] = (byte) i;
            if (sha1_32(buf) == -1993789508) {
                break;
            }
        }
        for (i = 1; i < 13; i++) {
            buf[987] = (byte) i;
            if (sha1_32(buf) == -510164013) {
                break;
            }
        }
        for (i = 125; i < 128; i++) {
            buf[988] = (byte) i;
            if (sha1_32(buf) == -138081163) {
                break;
            }
        }
        for (i = 17; i < 30; i++) {
            buf[989] = (byte) i;
            if (sha1_32(buf) == 38670255) {
                break;
            }
        }
        for (i = -78; i < -66; i++) {
            buf[990] = (byte) i;
            if (sha1_32(buf) == -186832523) {
                break;
            }
        }
        for (i = -52; i < -38; i++) {
            buf[991] = (byte) i;
            if (sha1_32(buf) == 1786645297) {
                break;
            }
        }
        for (i = 68; i < 94; i++) {
            buf[992] = (byte) i;
            if (sha1_32(buf) == -1044076298) {
                break;
            }
        }
        for (i = 96; i < 111; i++) {
            buf[993] = (byte) i;
            if (sha1_32(buf) == 1150059938) {
                break;
            }
        }
        for (i = 27; i < 40; i++) {
            buf[994] = (byte) i;
            if (sha1_32(buf) == 330223817) {
                break;
            }
        }
        for (i = -41; i < -21; i++) {
            buf[995] = (byte) i;
            if (sha1_32(buf) == -1840942634) {
                break;
            }
        }
        for (i = 99; i < 126; i++) {
            buf[996] = (byte) i;
            if (sha1_32(buf) == -1195435134) {
                break;
            }
        }
        for (i = -113; i < -102; i++) {
            buf[997] = (byte) i;
            if (sha1_32(buf) == 1668499049) {
                break;
            }
        }
        for (i = 54; i < 69; i++) {
            buf[998] = (byte) i;
            if (sha1_32(buf) == 839673677) {
                break;
            }
        }
        for (i = 20; i < 37; i++) {
            buf[999] = (byte) i;
            if (sha1_32(buf) == -1524545322) {
                break;
            }
        }
        for (i = 70; i < 96; i++) {
            buf[1000] = (byte) i;
            if (sha1_32(buf) == 1834499828) {
                break;
            }
        }
        for (i = 94; i < 102; i++) {
            buf[1001] = (byte) i;
            if (sha1_32(buf) == 83377692) {
                break;
            }
        }
        for (i = -128; i < -113; i++) {
            buf[1002] = (byte) i;
            if (sha1_32(buf) == 1065895154) {
                break;
            }
        }
        for (i = -122; i < -98; i++) {
            buf[1003] = (byte) i;
            if (sha1_32(buf) == 201639285) {
                break;
            }
        }
        for (i = 86; i < 95; i++) {
            buf[1004] = (byte) i;
            if (sha1_32(buf) == 1085299057) {
                break;
            }
        }
        for (i = -19; i < 0; i++) {
            buf[1005] = (byte) i;
            if (sha1_32(buf) == 948241230) {
                break;
            }
        }
        for (i = 96; i < 105; i++) {
            buf[1006] = (byte) i;
            if (sha1_32(buf) == 1003387087) {
                break;
            }
        }
        for (i = 5; i < 15; i++) {
            buf[1007] = (byte) i;
            if (sha1_32(buf) == -1802564858) {
                break;
            }
        }
        for (i = 66; i < 90; i++) {
            buf[1008] = (byte) i;
            if (sha1_32(buf) == 996265146) {
                break;
            }
        }
        for (i = 54; i < 65; i++) {
            buf[1009] = (byte) i;
            if (sha1_32(buf) == -1230293364) {
                break;
            }
        }
        for (i = -47; i < -25; i++) {
            buf[1010] = (byte) i;
            if (sha1_32(buf) == 57297147) {
                break;
            }
        }
        for (i = -114; i < -95; i++) {
            buf[1011] = (byte) i;
            if (sha1_32(buf) == -1532407620) {
                break;
            }
        }
        for (i = -79; i < -61; i++) {
            buf[1012] = (byte) i;
            if (sha1_32(buf) == -233354802) {
                break;
            }
        }
        for (i = 101; i < 122; i++) {
            buf[1013] = (byte) i;
            if (sha1_32(buf) == -21081941) {
                break;
            }
        }
        for (i = 13; i < 32; i++) {
            buf[1014] = (byte) i;
            if (sha1_32(buf) == -739995670) {
                break;
            }
        }
        for (i = -59; i < -52; i++) {
            buf[1015] = (byte) i;
            if (sha1_32(buf) == -1281203980) {
                break;
            }
        }
        for (i = -26; i < -16; i++) {
            buf[1016] = (byte) i;
            if (sha1_32(buf) == -987633716) {
                break;
            }
        }
        for (i = 8; i < 15; i++) {
            buf[1017] = (byte) i;
            if (sha1_32(buf) == -1391798417) {
                break;
            }
        }
        for (i = -76; i < -57; i++) {
            buf[1018] = (byte) i;
            if (sha1_32(buf) == -585292241) {
                break;
            }
        }
        for (i = 16; i < 44; i++) {
            buf[1019] = (byte) i;
            if (sha1_32(buf) == -425777405) {
                break;
            }
        }
        for (i = 113; i < 128; i++) {
            buf[1020] = (byte) i;
            if (sha1_32(buf) == 1047771493) {
                break;
            }
        }
        for (i = -55; i < -43; i++) {
            buf[1021] = (byte) i;
            if (sha1_32(buf) == -1248177022) {
                break;
            }
        }
        for (i = -3; i < 17; i++) {
            buf[1022] = (byte) i;
            if (sha1_32(buf) == 425548946) {
                break;
            }
        }
        for (i = -97; i < -67; i++) {
            buf[1023] = (byte) i;
            if (sha1_32(buf) == 585693630) {
                break;
            }
        }
        for (i = 47; i < 62; i++) {
            buf[1024] = (byte) i;
            if (sha1_32(buf) == -67135399) {
                break;
            }
        }
        for (i = -121; i < -119; i++) {
            buf[1025] = (byte) i;
            if (sha1_32(buf) == 1075803908) {
                break;
            }
        }
        for (i = 104; i < 115; i++) {
            buf[1026] = (byte) i;
            if (sha1_32(buf) == -1720652371) {
                break;
            }
        }
        for (i = 83; i < 89; i++) {
            buf[1027] = (byte) i;
            if (sha1_32(buf) == 1587985056) {
                break;
            }
        }
        for (i = 83; i < 88; i++) {
            buf[1028] = (byte) i;
            if (sha1_32(buf) == -307769694) {
                break;
            }
        }
        for (i = -17; i < -9; i++) {
            buf[1029] = (byte) i;
            if (sha1_32(buf) == 1990376643) {
                break;
            }
        }
        for (i = 7; i < 15; i++) {
            buf[1030] = (byte) i;
            if (sha1_32(buf) == -161363829) {
                break;
            }
        }
        for (i = 70; i < 89; i++) {
            buf[1031] = (byte) i;
            if (sha1_32(buf) == 972315070) {
                break;
            }
        }
        for (i = -128; i < -109; i++) {
            buf[1032] = (byte) i;
            if (sha1_32(buf) == -325742201) {
                break;
            }
        }
        for (i = 99; i < 117; i++) {
            buf[1033] = (byte) i;
            if (sha1_32(buf) == -1951170378) {
                break;
            }
        }
        for (i = -125; i < -100; i++) {
            buf[1034] = (byte) i;
            if (sha1_32(buf) == -385406945) {
                break;
            }
        }
        for (i = 44; i < 63; i++) {
            buf[1035] = (byte) i;
            if (sha1_32(buf) == -1138360025) {
                break;
            }
        }
        for (i = -110; i < -86; i++) {
            buf[1036] = (byte) i;
            if (sha1_32(buf) == -1549541432) {
                break;
            }
        }
        for (i = 78; i < 99; i++) {
            buf[1037] = (byte) i;
            if (sha1_32(buf) == 797448560) {
                break;
            }
        }
        for (i = 103; i < 128; i++) {
            buf[1038] = (byte) i;
            if (sha1_32(buf) == -273631720) {
                break;
            }
        }
        for (i = 80; i < 102; i++) {
            buf[1039] = (byte) i;
            if (sha1_32(buf) == -1872361925) {
                break;
            }
        }
        for (i = -95; i < -82; i++) {
            buf[1040] = (byte) i;
            if (sha1_32(buf) == -789147217) {
                break;
            }
        }
        for (i = 98; i < 117; i++) {
            buf[1041] = (byte) i;
            if (sha1_32(buf) == 797975171) {
                break;
            }
        }
        for (i = 93; i < 107; i++) {
            buf[1042] = (byte) i;
            if (sha1_32(buf) == -935001624) {
                break;
            }
        }
        for (i = -128; i < -117; i++) {
            buf[1043] = (byte) i;
            if (sha1_32(buf) == -1000660199) {
                break;
            }
        }
        for (i = 122; i < 128; i++) {
            buf[1044] = (byte) i;
            if (sha1_32(buf) == -1690882890) {
                break;
            }
        }
        for (i = -90; i < -70; i++) {
            buf[1045] = (byte) i;
            if (sha1_32(buf) == 190097593) {
                break;
            }
        }
        for (i = 3; i < 22; i++) {
            buf[1046] = (byte) i;
            if (sha1_32(buf) == -376783196) {
                break;
            }
        }
        for (i = 39; i < 70; i++) {
            buf[1047] = (byte) i;
            if (sha1_32(buf) == 255970710) {
                break;
            }
        }
        for (i = 76; i < 100; i++) {
            buf[1048] = (byte) i;
            if (sha1_32(buf) == 1423706546) {
                break;
            }
        }
        for (i = -116; i < -107; i++) {
            buf[1049] = (byte) i;
            if (sha1_32(buf) == -1845806360) {
                break;
            }
        }
        for (i = -35; i < -8; i++) {
            buf[1050] = (byte) i;
            if (sha1_32(buf) == -2032137354) {
                break;
            }
        }
        for (i = 80; i < 93; i++) {
            buf[1051] = (byte) i;
            if (sha1_32(buf) == 294033314) {
                break;
            }
        }
        for (i = 78; i < 95; i++) {
            buf[1052] = (byte) i;
            if (sha1_32(buf) == 808453445) {
                break;
            }
        }
        for (i = -9; i < -2; i++) {
            buf[1053] = (byte) i;
            if (sha1_32(buf) == 553724930) {
                break;
            }
        }
        for (i = -24; i < -11; i++) {
            buf[1054] = (byte) i;
            if (sha1_32(buf) == -1199741188) {
                break;
            }
        }
        for (i = 32; i < 53; i++) {
            buf[1055] = (byte) i;
            if (sha1_32(buf) == -926153570) {
                break;
            }
        }
        for (i = 104; i < 125; i++) {
            buf[1056] = (byte) i;
            if (sha1_32(buf) == -1906721460) {
                break;
            }
        }
        for (i = 113; i < 128; i++) {
            buf[1057] = (byte) i;
            if (sha1_32(buf) == -267550424) {
                break;
            }
        }
        for (i = -39; i < -24; i++) {
            buf[1058] = (byte) i;
            if (sha1_32(buf) == -1069178950) {
                break;
            }
        }
        for (i = 123; i < 128; i++) {
            buf[1059] = (byte) i;
            if (sha1_32(buf) == 1174845823) {
                break;
            }
        }
        for (i = 14; i < 28; i++) {
            buf[1060] = (byte) i;
            if (sha1_32(buf) == 309689759) {
                break;
            }
        }
        for (i = 27; i < 49; i++) {
            buf[1061] = (byte) i;
            if (sha1_32(buf) == 213194133) {
                break;
            }
        }
        for (i = -117; i < -93; i++) {
            buf[1062] = (byte) i;
            if (sha1_32(buf) == -1567680723) {
                break;
            }
        }
        for (i = 97; i < 115; i++) {
            buf[1063] = (byte) i;
            if (sha1_32(buf) == -1432960398) {
                break;
            }
        }
        for (i = 25; i < 35; i++) {
            buf[1064] = (byte) i;
            if (sha1_32(buf) == -372940527) {
                break;
            }
        }
        for (i = 75; i < 92; i++) {
            buf[1065] = (byte) i;
            if (sha1_32(buf) == -1725152678) {
                break;
            }
        }
        for (i = -51; i < -38; i++) {
            buf[1066] = (byte) i;
            if (sha1_32(buf) == 1246548759) {
                break;
            }
        }
        for (i = 120; i < 128; i++) {
            buf[1067] = (byte) i;
            if (sha1_32(buf) == 1968085303) {
                break;
            }
        }
        for (i = 18; i < 24; i++) {
            buf[1068] = (byte) i;
            if (sha1_32(buf) == 772854610) {
                break;
            }
        }
        for (i = -128; i < -117; i++) {
            buf[1069] = (byte) i;
            if (sha1_32(buf) == 539385836) {
                break;
            }
        }
        for (i = 4; i < 20; i++) {
            buf[1070] = (byte) i;
            if (sha1_32(buf) == 354267377) {
                break;
            }
        }
        for (i = -117; i < -104; i++) {
            buf[1071] = (byte) i;
            if (sha1_32(buf) == 1501274308) {
                break;
            }
        }
        for (i = 113; i < 119; i++) {
            buf[1072] = (byte) i;
            if (sha1_32(buf) == 1234653325) {
                break;
            }
        }
        for (i = -83; i < -65; i++) {
            buf[1073] = (byte) i;
            if (sha1_32(buf) == 1787610465) {
                break;
            }
        }
        for (i = -29; i < -11; i++) {
            buf[1074] = (byte) i;
            if (sha1_32(buf) == 279711134) {
                break;
            }
        }
        for (i = 87; i < 110; i++) {
            buf[1075] = (byte) i;
            if (sha1_32(buf) == 1290191417) {
                break;
            }
        }
        for (i = -70; i < -53; i++) {
            buf[1076] = (byte) i;
            if (sha1_32(buf) == -1372488777) {
                break;
            }
        }
        for (i = 54; i < 84; i++) {
            buf[1077] = (byte) i;
            if (sha1_32(buf) == 2130344519) {
                break;
            }
        }
        for (i = 97; i < 117; i++) {
            buf[1078] = (byte) i;
            if (sha1_32(buf) == -1176603696) {
                break;
            }
        }
        for (i = 99; i < 120; i++) {
            buf[1079] = (byte) i;
            if (sha1_32(buf) == 2021821190) {
                break;
            }
        }
        for (i = -70; i < -46; i++) {
            buf[1080] = (byte) i;
            if (sha1_32(buf) == -1826704177) {
                break;
            }
        }
        for (i = -5; i < 10; i++) {
            buf[1081] = (byte) i;
            if (sha1_32(buf) == -1294517731) {
                break;
            }
        }
        for (i = -67; i < -42; i++) {
            buf[1082] = (byte) i;
            if (sha1_32(buf) == -320623231) {
                break;
            }
        }
        for (i = 118; i < 128; i++) {
            buf[1083] = (byte) i;
            if (sha1_32(buf) == -220922323) {
                break;
            }
        }
        for (i = -128; i < -111; i++) {
            buf[1084] = (byte) i;
            if (sha1_32(buf) == -381818175) {
                break;
            }
        }
        for (i = -126; i < -106; i++) {
            buf[1085] = (byte) i;
            if (sha1_32(buf) == 1618744464) {
                break;
            }
        }
        for (i = 84; i < 107; i++) {
            buf[1086] = (byte) i;
            if (sha1_32(buf) == 1696396530) {
                break;
            }
        }
        for (i = -27; i < -7; i++) {
            buf[1087] = (byte) i;
            if (sha1_32(buf) == 1271759048) {
                break;
            }
        }
        for (i = -51; i < -45; i++) {
            buf[1088] = (byte) i;
            if (sha1_32(buf) == 1826718702) {
                break;
            }
        }
        for (i = 35; i < 43; i++) {
            buf[1089] = (byte) i;
            if (sha1_32(buf) == -425845769) {
                break;
            }
        }
        for (i = -127; i < -107; i++) {
            buf[1090] = (byte) i;
            if (sha1_32(buf) == 1589708662) {
                break;
            }
        }
        for (i = -77; i < -69; i++) {
            buf[1091] = (byte) i;
            if (sha1_32(buf) == 1878501742) {
                break;
            }
        }
        for (i = -84; i < -65; i++) {
            buf[1092] = (byte) i;
            if (sha1_32(buf) == 1705505680) {
                break;
            }
        }
        for (i = 117; i < 128; i++) {
            buf[1093] = (byte) i;
            if (sha1_32(buf) == 1929735699) {
                break;
            }
        }
        for (i = -128; i < -120; i++) {
            buf[1094] = (byte) i;
            if (sha1_32(buf) == -1622742816) {
                break;
            }
        }
        for (i = -79; i < -61; i++) {
            buf[1095] = (byte) i;
            if (sha1_32(buf) == 82011211) {
                break;
            }
        }
        for (i = -22; i < -7; i++) {
            buf[1096] = (byte) i;
            if (sha1_32(buf) == -1572185041) {
                break;
            }
        }
        for (i = -2; i < 13; i++) {
            buf[1097] = (byte) i;
            if (sha1_32(buf) == 954151314) {
                break;
            }
        }
        for (i = 60; i < 69; i++) {
            buf[1098] = (byte) i;
            if (sha1_32(buf) == 1628389818) {
                break;
            }
        }
        for (i = -67; i < -60; i++) {
            buf[1099] = (byte) i;
            if (sha1_32(buf) == 55671770) {
                break;
            }
        }
        for (i = -21; i < -10; i++) {
            buf[1100] = (byte) i;
            if (sha1_32(buf) == 444165836) {
                break;
            }
        }
        for (i = -71; i < -64; i++) {
            buf[1101] = (byte) i;
            if (sha1_32(buf) == -1622081725) {
                break;
            }
        }
        for (i = -52; i < -31; i++) {
            buf[1102] = (byte) i;
            if (sha1_32(buf) == 1579892095) {
                break;
            }
        }
        for (i = 37; i < 57; i++) {
            buf[1103] = (byte) i;
            if (sha1_32(buf) == 953167046) {
                break;
            }
        }
        for (i = -6; i < 8; i++) {
            buf[1104] = (byte) i;
            if (sha1_32(buf) == 421043300) {
                break;
            }
        }
        for (i = -80; i < -73; i++) {
            buf[1105] = (byte) i;
            if (sha1_32(buf) == 440003988) {
                break;
            }
        }
        for (i = 58; i < 80; i++) {
            buf[1106] = (byte) i;
            if (sha1_32(buf) == -1882591436) {
                break;
            }
        }
        for (i = -70; i < -57; i++) {
            buf[1107] = (byte) i;
            if (sha1_32(buf) == 735217512) {
                break;
            }
        }
        for (i = 60; i < 74; i++) {
            buf[1108] = (byte) i;
            if (sha1_32(buf) == -1077049139) {
                break;
            }
        }
        for (i = -12; i < 7; i++) {
            buf[1109] = (byte) i;
            if (sha1_32(buf) == -1103204972) {
                break;
            }
        }
        for (i = -50; i < -28; i++) {
            buf[1110] = (byte) i;
            if (sha1_32(buf) == 1297283718) {
                break;
            }
        }
        for (i = 77; i < 88; i++) {
            buf[1111] = (byte) i;
            if (sha1_32(buf) == -669157094) {
                break;
            }
        }
        for (i = -44; i < -25; i++) {
            buf[1112] = (byte) i;
            if (sha1_32(buf) == 1021789273) {
                break;
            }
        }
        for (i = 60; i < 72; i++) {
            buf[1113] = (byte) i;
            if (sha1_32(buf) == -1687144725) {
                break;
            }
        }
        for (i = -8; i < -1; i++) {
            buf[1114] = (byte) i;
            if (sha1_32(buf) == -2044669978) {
                break;
            }
        }
        for (i = 75; i < 85; i++) {
            buf[1115] = (byte) i;
            if (sha1_32(buf) == -1669966235) {
                break;
            }
        }
        for (i = -118; i < -103; i++) {
            buf[1116] = (byte) i;
            if (sha1_32(buf) == -1859516984) {
                break;
            }
        }
        for (i = 120; i < 126; i++) {
            buf[1117] = (byte) i;
            if (sha1_32(buf) == -1135081189) {
                break;
            }
        }
        for (i = -127; i < -112; i++) {
            buf[1118] = (byte) i;
            if (sha1_32(buf) == -260716181) {
                break;
            }
        }
        for (i = 22; i < 46; i++) {
            buf[1119] = (byte) i;
            if (sha1_32(buf) == -58019470) {
                break;
            }
        }
        for (i = 9; i < 26; i++) {
            buf[1120] = (byte) i;
            if (sha1_32(buf) == -1444904922) {
                break;
            }
        }
        for (i = 95; i < 109; i++) {
            buf[1121] = (byte) i;
            if (sha1_32(buf) == 299023988) {
                break;
            }
        }
        return buf;
    }
}
