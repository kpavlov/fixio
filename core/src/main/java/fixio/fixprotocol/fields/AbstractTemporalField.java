package fixio.fixprotocol.fields;

public abstract class AbstractTemporalField extends AbstractField<Long> {

    protected final long value;

    public AbstractTemporalField(int tagNum, long timestampMillis) {
        super(tagNum);
        this.value = timestampMillis;
    }

    /**
     * Returns timestamp value in milliseconds.
     * Use with caution, since it causes value autoboxing.
     *
     * @return timestamp value in milliseconds.
     * @see #timestampMillis()
     */
    @Override
    public final Long getValue() {
        return value;
    }

    /**
     * Useful when you need to get a milliseconds without autoboxing
     */
    public final long timestampMillis() {
        return value;
    }
}
