package org.jboss.netty.bootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.ExternalResourceReleasable;

public class Bootstrap implements ExternalResourceReleasable {
    private static final short[] ORDER_TEST_SAMPLES = new short[]{(short) 682, (short) 807, (short) 637, (short) 358, (short) 570, (short) 828, (short) 407, (short) 319, (short) 105, (short) 41, (short) 563, (short) 544, (short) 518, (short) 298, (short) 418, (short) 50, (short) 156, (short) 769, (short) 984, (short) 503, (short) 191, (short) 578, (short) 309, (short) 710, (short) 327, (short) 720, (short) 591, (short) 939, (short) 374, (short) 707, (short) 43, (short) 463, (short) 227, (short) 174, (short) 30, (short) 531, (short) 135, (short) 930, (short) 190, (short) 823, (short) 925, (short) 835, (short) 328, (short) 239, (short) 415, (short) 500, (short) 144, (short) 460, (short) 83, (short) 774, (short) 921, (short) 4, (short) 95, (short) 468, (short) 687, (short) 493, (short) 991, (short) 436, (short) 245, (short) 742, (short) 149, (short) 821, (short) 142, (short) 782, (short) 297, (short) 918, (short) 917, (short) 424, (short) 978, (short) 992, (short) 79, (short) 906, (short) 535, (short) 515, (short) 850, (short) 80, (short) 125, (short) 378, (short) 307, (short) 883, (short) 836, (short) 160, (short) 27, (short) 630, (short) 668, (short) 226, (short) 560, (short) 698, (short) 467, (short) 829, (short) 476, (short) 163, (short) 977, (short) 367, (short) 325, (short) 184, (short) 204, (short) 312, (short) 486, (short) 53, (short) 179, (short) 592, (short) 252, (short) 750, (short) 893, (short) 517, (short) 937, (short) 124, (short) 148, (short) 719, (short) 973, (short) 566, (short) 405, (short) 449, (short) 452, (short) 777, (short) 349, (short) 761, (short) 167, (short) 783, (short) 220, (short) 802, (short) 117, (short) 604, (short) 216, (short) 363, (short) 120, (short) 621, (short) 219, (short) 182, (short) 817, (short) 244, (short) 438, (short) 465, (short) 934, (short) 888, (short) 628, (short) 209, (short) 631, (short) 17, (short) 870, (short) 679, (short) 826, (short) 945, (short) 680, (short) 848, (short) 974, (short) 573, (short) 626, (short) 865, (short) 109, (short) 317, (short) 91, (short) 494, (short) 965, (short) 473, (short) 725, (short) 388, (short) 302, (short) 936, (short) 660, (short) 150, (short) 122, (short) 949, (short) 295, (short) 392, (short) 63, (short) 634, (short) 772, (short) 143, (short) 990, (short) 895, (short) 538, (short) 59, (short) 541, (short) 32, (short) 669, (short) 321, (short) 811, (short) 756, (short) 82, (short) 955, (short) 953, (short) 636, (short) 390, (short) 162, (short) 688, (short) 444, (short) 70, (short) 590, (short) 183, (short) 745, (short) 543, (short) 666, (short) 951, (short) 642, (short) 747, (short) 765, (short) 98, (short) 469, (short) 884, (short) 929, (short) 178, (short) 721, (short) 994, (short) 840, (short) 353, (short) 726, (short) 940, (short) 759, (short) 624, (short) 919, (short) 667, (short) 629, (short) 272, (short) 979, (short) 326, (short) 608, (short) 453, (short) 11, (short) 322, (short) 347, (short) 647, (short) 354, (short) 381, (short) 746, (short) 472, (short) 890, (short) 249, (short) 536, (short) 733, (short) 404, (short) 170, (short) 959, (short) 34, (short) 899, (short) 195, (short) 651, (short) 140, (short) 856, (short) 201, (short) 237, (short) 51, (short) 933, (short) 268, (short) 849, (short) 294, (short) 115, (short) 157, (short) 14, (short) 854, (short) 373, (short) 186, (short) 872, (short) 71, (short) 523, (short) 931, (short) 952, (short) 655, (short) 561, (short) 607, (short) 862, (short) 554, (short) 661, (short) 313, (short) 909, (short) 511, (short) 752, (short) 986, (short) 311, (short) 287, (short) 775, (short) 505, (short) 878, (short) 422, (short) 103, (short) 299, (short) 119, (short) 107, (short) 344, (short) 487, (short) 776, (short) 445, (short) 218, (short) 549, (short) 697, (short) 454, (short) 6, (short) 462, (short) 455, (short) 52, (short) 481, (short) 594, (short) 126, (short) 112, (short) 66, (short) 877, (short) 172, (short) 153, (short) 912, (short) 834, (short) 741, (short) 610, (short) 915, (short) 964, (short) 831, (short) 575, (short) 714, (short) 250, (short) 461, (short) 814, (short) 913, (short) 369, (short) 542, (short) 882, (short) 851, (short) 427, (short) 838, (short) 867, (short) 507, (short) 434, (short) 569, (short) 20, (short) 950, (short) 792, (short) 605, (short) 798, (short) 962, (short) 923, (short) 258, (short) 972, (short) 762, (short) 809, (short) 843, (short) 674, (short) 448, (short) 280, (short) 495, (short) 285, (short) 822, (short) 283, (short) 147, (short) 451, (short) 993, (short) 794, (short) 982, (short) 748, (short) 189, (short) 274, (short) 96, (short) 73, (short) 810, (short) 401, (short) 261, (short) 277, (short) 346, (short) 527, (short) 645, (short) 601, (short) 868, (short) 248, (short) 879, (short) 371, (short) 428, (short) 559, (short) 278, (short) 265, (short) 62, (short) 225, (short) 853, (short) 483, (short) 771, (short) 9, (short) 8, (short) 339, (short) 653, (short) 263, (short) 28, (short) 477, (short) 995, (short) 208, (short) 880, (short) 292, (short) 480, (short) 516, (short) 457, (short) 286, (short) 897, (short) 21, (short) 852, (short) 971, (short) 658, (short) 623, (short) 528, (short) 316, (short) 471, (short) 860, (short) 306, (short) 638, (short) 711, (short) 875, (short) 671, (short) 108, (short) 158, (short) 646, (short) 24, (short) 257, (short) 724, (short) 193, (short) 341, (short) 902, (short) 599, (short) 565, (short) 334, (short) 506, (short) 684, (short) 960, (short) 780, (short) 429, (short) 801, (short) 910, (short) 308, (short) 383, (short) 901, (short) 489, (short) 81, (short) 512, (short) 164, (short) 755, (short) 514, (short) 723, (short) 141, (short) 296, (short) 958, (short) 686, (short) 15, (short) 799, (short) 579, (short) 598, (short) 558, (short) 414, (short) 64, (short) 420, (short) 730, (short) 256, (short) 131, (short) 45, (short) 129, (short) 259, (short) 338, (short) 999, (short) 175, (short) 740, (short) 790, (short) 324, (short) 985, (short) 896, (short) 482, (short) 841, (short) 606, (short) 377, (short) 111, (short) 372, (short) 699, (short) 988, (short) 233, (short) 243, (short) 203, (short) 781, (short) 969, (short) 903, (short) 662, (short) 632, (short) 301, (short) 44, (short) 981, (short) 36, (short) 412, (short) 946, (short) 816, (short) 284, (short) 447, (short) 214, (short) 672, (short) 758, (short) 954, (short) 804, (short) 2, (short) 928, (short) 886, (short) 421, (short) 596, (short) 574, (short) 16, (short) 892, (short) 68, (short) 546, (short) 522, (short) 490, (short) 873, (short) 656, (short) 696, (short) 864, (short) 130, (short) 40, (short) 393, (short) 926, (short) 394, (short) 932, (short) 876, (short) 664, (short) 293, (short) 154, (short) 916, (short) 55, (short) 196, (short) 842, (short) 498, (short) 177, (short) 948, (short) 540, (short) 127, (short) 271, (short) 113, (short) 844, (short) 576, (short) 132, (short) 943, (short) 12, (short) 123, (short) 291, (short) 31, (short) 212, (short) 529, (short) 547, (short) 171, (short) 582, (short) 609, (short) 793, (short) 830, (short) 221, (short) 440, (short) 568, (short) 118, (short) 406, (short) 194, (short) 827, (short) 360, (short) 622, (short) 389, (short) 800, (short) 571, (short) 213, (short) 262, (short) 403, (short) 408, (short) 881, (short) 289, (short) 635, (short) 967, (short) 432, (short) 376, (short) 649, (short) 832, (short) 857, (short) 717, (short) 145, (short) 510, (short) 159, (short) 980, (short) 683, (short) 580, (short) 484, (short) 379, (short) 246, (short) 88, (short) 567, (short) 320, (short) 643, (short) 7, (short) 924, (short) 397, (short) 10, (short) 787, (short) 845, (short) 779, (short) 670, (short) 716, (short) 19, (short) 600, (short) 382, (short) 0, (short) 210, (short) 665, (short) 228, (short) 97, (short) 266, (short) 90, (short) 304, (short) 456, (short) 180, (short) 152, (short) 425, (short) 310, (short) 768, (short) 223, (short) 702, (short) 997, (short) 577, (short) 663, (short) 290, (short) 537, (short) 416, (short) 426, (short) 914, (short) 691, (short) 23, (short) 281, (short) 497, (short) 508, (short) 48, (short) 681, (short) 581, (short) 728, (short) 99, (short) 795, (short) 530, (short) 871, (short) 957, (short) 889, (short) 206, (short) 813, (short) 839, (short) 709, (short) 805, (short) 253, (short) 151, (short) 613, (short) 65, (short) 654, (short) 93, (short) 639, (short) 784, (short) 891, (short) 352, (short) 67, (short) 430, (short) 754, (short) 76, (short) 187, (short) 443, (short) 676, (short) 362, (short) 961, (short) 874, (short) 330, (short) 331, (short) 384, (short) 85, (short) 217, (short) 855, (short) 818, (short) 738, (short) 361, (short) 314, (short) 3, (short) 615, (short) 520, (short) 355, (short) 920, (short) 689, (short) 22, (short) 188, (short) 49, (short) 904, (short) 935, (short) 136, (short) 475, (short) 693, (short) 749, (short) 519, (short) 812, (short) 100, (short) 207, (short) 963, (short) 364, (short) 464, (short) 572, (short) 731, (short) 230, (short) 833, (short) 385, (short) 499, (short) 545, (short) 273, (short) 232, (short) 398, (short) 478, (short) 975, (short) 564, (short) 399, (short) 504, (short) 35, (short) 562, (short) 938, (short) 211, (short) 26, (short) 337, (short) 54, (short) 614, (short) 586, (short) 433, (short) 450, (short) 763, (short) 238, (short) 305, (short) 941, (short) 370, (short) 885, (short) 837, (short) 234, (short) 110, (short) 137, (short) 395, (short) 368, (short) 695, (short) 342, (short) 907, (short) 396, (short) 474, (short) 176, (short) 737, (short) 796, (short) 446, (short) 37, (short) 894, (short) 727, (short) 648, (short) 431, (short) 1, (short) 366, (short) 525, (short) 553, (short) 704, (short) 329, (short) 627, (short) 479, (short) 33, (short) 492, (short) 260, (short) 241, (short) 86, (short) 185, (short) 491, (short) 966, (short) 247, (short) 13, (short) 587, (short) 602, (short) 409, (short) 335, (short) 650, (short) 235, (short) 611, (short) 470, (short) 442, (short) 597, (short) 254, (short) 343, (short) 539, (short) 146, (short) 585, (short) 593, (short) 641, (short) 770, (short) 94, (short) 976, (short) 705, (short) 181, (short) 255, (short) 315, (short) 718, (short) 526, (short) 987, (short) 692, (short) 983, (short) 595, (short) 898, (short) 282, (short) 133, (short) 439, (short) 633, (short) 534, (short) 861, (short) 269, (short) 619, (short) 677, (short) 502, (short) 375, (short) 224, (short) 806, (short) 869, (short) 417, (short) 584, (short) 612, (short) 803, (short) 58, (short) 84, (short) 788, (short) 797, (short) 38, (short) 700, (short) 751, (short) 603, (short) 652, (short) 57, (short) 240, (short) 947, (short) 350, (short) 270, (short) 333, (short) 116, (short) 736, (short) 69, (short) 74, (short) 104, (short) 767, (short) 318, (short) 735, (short) 859, (short) 357, (short) 555, (short) 411, (short) 267, (short) 712, (short) 675, (short) 532, (short) 825, (short) 496, (short) 927, (short) 942, (short) 102, (short) 46, (short) 192, (short) 114, (short) 744, (short) 138, (short) 998, (short) 72, (short) 617, (short) 134, (short) 846, (short) 166, (short) 77, (short) 900, (short) 5, (short) 303, (short) 387, (short) 400, (short) 47, (short) 729, (short) 922, (short) 222, (short) 197, (short) 351, (short) 509, (short) 524, (short) 165, (short) 485, (short) 300, (short) 944, (short) 380, (short) 625, (short) 778, (short) 685, (short) 29, (short) 589, (short) 766, (short) 161, (short) 391, (short) 423, (short) 42, (short) 734, (short) 552, (short) 215, (short) 824, (short) 908, (short) 229, (short) 89, (short) 251, (short) 199, (short) 616, (short) 78, (short) 644, (short) 242, (short) 722, (short) 25, (short) 437, (short) 732, (short) 956, (short) 275, (short) 200, (short) 970, (short) 753, (short) 791, (short) 336, (short) 556, (short) 847, (short) 703, (short) 236, (short) 715, (short) 75, (short) 863, (short) 713, (short) 785, (short) 911, (short) 786, (short) 620, (short) 551, (short) 413, (short) 39, (short) 739, (short) 820, (short) 808, (short) 764, (short) 701, (short) 819, (short) 173, (short) 989, (short) 345, (short) 690, (short) 459, (short) 60, (short) 106, (short) 887, (short) 996, (short) 365, (short) 673, (short) 968, (short) 513, (short) 18, (short) 419, (short) 550, (short) 588, (short) 435, (short) 264, (short) 789, (short) 340, (short) 659, (short) 466, (short) 356, (short) 288, (short) 56, (short) 708, (short) 557, (short) 488, (short) 760, (short) 332, (short) 402, (short) 168, (short) 202, (short) 521, (short) 757, (short) 205, (short) 706, (short) 441, (short) 773, (short) 231, (short) 583, (short) 386, (short) 678, (short) 618, (short) 815, (short) 279, (short) 87, (short) 533, (short) 61, (short) 548, (short) 92, (short) 169, (short) 694, (short) 905, (short) 198, (short) 121, (short) 410, (short) 139, (short) 657, (short) 640, (short) 743, (short) 128, (short) 458, (short) 866, (short) 501, (short) 348, (short) 155, (short) 276, (short) 101, (short) 858, (short) 323, (short) 359};
    private volatile ChannelFactory factory;
    private volatile Map<String, Object> options = new HashMap();
    private volatile ChannelPipeline pipeline = Channels.pipeline();
    private volatile ChannelPipelineFactory pipelineFactory = Channels.pipelineFactory(this.pipeline);

    protected Bootstrap() {
    }

    protected Bootstrap(ChannelFactory channelFactory) {
        setFactory(channelFactory);
    }

    public ChannelFactory getFactory() {
        ChannelFactory factory = this.factory;
        if (factory != null) {
            return factory;
        }
        throw new IllegalStateException("factory is not set yet.");
    }

    public void setFactory(ChannelFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        } else if (this.factory != null) {
            throw new IllegalStateException("factory can't change once set.");
        } else {
            this.factory = factory;
        }
    }

    public ChannelPipeline getPipeline() {
        ChannelPipeline pipeline = this.pipeline;
        if (pipeline != null) {
            return pipeline;
        }
        throw new IllegalStateException("getPipeline() cannot be called if setPipelineFactory() was called.");
    }

    public void setPipeline(ChannelPipeline pipeline) {
        if (pipeline == null) {
            throw new NullPointerException("pipeline");
        }
        this.pipeline = pipeline;
        this.pipelineFactory = Channels.pipelineFactory(pipeline);
    }

    public Map<String, ChannelHandler> getPipelineAsMap() {
        ChannelPipeline pipeline = this.pipeline;
        if (pipeline != null) {
            return pipeline.toMap();
        }
        throw new IllegalStateException("pipelineFactory in use");
    }

    public void setPipelineAsMap(Map<String, ChannelHandler> pipelineMap) {
        if (pipelineMap == null) {
            throw new NullPointerException("pipelineMap");
        } else if (isOrderedMap(pipelineMap)) {
            ChannelPipeline pipeline = Channels.pipeline();
            for (Entry<String, ChannelHandler> e : pipelineMap.entrySet()) {
                pipeline.addLast((String) e.getKey(), (ChannelHandler) e.getValue());
            }
            setPipeline(pipeline);
        } else {
            throw new IllegalArgumentException("pipelineMap is not an ordered map. Please use " + LinkedHashMap.class.getName() + '.');
        }
    }

    public ChannelPipelineFactory getPipelineFactory() {
        return this.pipelineFactory;
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        if (pipelineFactory == null) {
            throw new NullPointerException("pipelineFactory");
        }
        this.pipeline = null;
        this.pipelineFactory = pipelineFactory;
    }

    public Map<String, Object> getOptions() {
        return new TreeMap(this.options);
    }

    public void setOptions(Map<String, Object> options) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        this.options = new HashMap(options);
    }

    public Object getOption(String key) {
        if (key != null) {
            return this.options.get(key);
        }
        throw new NullPointerException("key");
    }

    public void setOption(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key");
        } else if (value == null) {
            this.options.remove(key);
        } else {
            this.options.put(key, value);
        }
    }

    public void releaseExternalResources() {
        ChannelFactory factory = this.factory;
        if (factory != null) {
            factory.releaseExternalResources();
        }
    }

    public void shutdown() {
        ChannelFactory factory = this.factory;
        if (factory != null) {
            factory.shutdown();
        }
    }

    static boolean isOrderedMap(Map<?, ?> map) {
        Class<?> mapType = map.getClass();
        if (LinkedHashMap.class.isAssignableFrom(mapType)) {
            return true;
        }
        for (Class<?> type = mapType; type != null; type = type.getSuperclass()) {
            for (Class<?> i : type.getInterfaces()) {
                if (i.getName().endsWith("OrderedMap")) {
                    return true;
                }
            }
        }
        try {
            Map<Object, Object> newMap = (Map) mapType.newInstance();
            List<String> expectedKeys = new ArrayList();
            String dummyValue = "dummyValue";
            for (short element : ORDER_TEST_SAMPLES) {
                String key = String.valueOf(element);
                newMap.put(key, dummyValue);
                expectedKeys.add(key);
                Iterator<String> it = expectedKeys.iterator();
                for (Object actualKey : newMap.keySet()) {
                    if (!((String) it.next()).equals(actualKey)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
