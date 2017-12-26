package org.jboss.netty.channel;

public class AdaptiveReceiveBufferSizePredictorFactory implements ReceiveBufferSizePredictorFactory {
    private final int initial;
    private final int maximum;
    private final int minimum;

    public AdaptiveReceiveBufferSizePredictorFactory() {
        this(64, 1024, 65536);
    }

    public AdaptiveReceiveBufferSizePredictorFactory(int minimum, int initial, int maximum) {
        if (minimum <= 0) {
            throw new IllegalArgumentException("minimum: " + minimum);
        } else if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        } else if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        } else {
            this.minimum = minimum;
            this.initial = initial;
            this.maximum = maximum;
        }
    }

    public ReceiveBufferSizePredictor getPredictor() throws Exception {
        return new AdaptiveReceiveBufferSizePredictor(this.minimum, this.initial, this.maximum);
    }
}
