/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.wikipedia.miner.extract.model.struct;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class LabelCount extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"LabelCount\",\"namespace\":\"org.wikipedia.miner.extract.model.struct\",\"fields\":[{\"name\":\"label\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence label;
  @Deprecated public int count;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use {@link \#newBuilder()}. 
   */
  public LabelCount() {}

  /**
   * All-args constructor.
   */
  public LabelCount(java.lang.CharSequence label, java.lang.Integer count) {
    this.label = label;
    this.count = count;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return label;
    case 1: return count;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: label = (java.lang.CharSequence)value$; break;
    case 1: count = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'label' field.
   */
  public java.lang.CharSequence getLabel() {
    return label;
  }

  /**
   * Sets the value of the 'label' field.
   * @param value the value to set.
   */
  public void setLabel(java.lang.CharSequence value) {
    this.label = value;
  }

  /**
   * Gets the value of the 'count' field.
   */
  public java.lang.Integer getCount() {
    return count;
  }

  /**
   * Sets the value of the 'count' field.
   * @param value the value to set.
   */
  public void setCount(java.lang.Integer value) {
    this.count = value;
  }

  /** Creates a new LabelCount RecordBuilder */
  public static org.wikipedia.miner.extract.model.struct.LabelCount.Builder newBuilder() {
    return new org.wikipedia.miner.extract.model.struct.LabelCount.Builder();
  }
  
  /** Creates a new LabelCount RecordBuilder by copying an existing Builder */
  public static org.wikipedia.miner.extract.model.struct.LabelCount.Builder newBuilder(org.wikipedia.miner.extract.model.struct.LabelCount.Builder other) {
    return new org.wikipedia.miner.extract.model.struct.LabelCount.Builder(other);
  }
  
  /** Creates a new LabelCount RecordBuilder by copying an existing LabelCount instance */
  public static org.wikipedia.miner.extract.model.struct.LabelCount.Builder newBuilder(org.wikipedia.miner.extract.model.struct.LabelCount other) {
    return new org.wikipedia.miner.extract.model.struct.LabelCount.Builder(other);
  }
  
  /**
   * RecordBuilder for LabelCount instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<LabelCount>
    implements org.apache.avro.data.RecordBuilder<LabelCount> {

    private java.lang.CharSequence label;
    private int count;

    /** Creates a new Builder */
    private Builder() {
      super(org.wikipedia.miner.extract.model.struct.LabelCount.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.wikipedia.miner.extract.model.struct.LabelCount.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.label)) {
        this.label = data().deepCopy(fields()[0].schema(), other.label);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.count)) {
        this.count = data().deepCopy(fields()[1].schema(), other.count);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing LabelCount instance */
    private Builder(org.wikipedia.miner.extract.model.struct.LabelCount other) {
            super(org.wikipedia.miner.extract.model.struct.LabelCount.SCHEMA$);
      if (isValidValue(fields()[0], other.label)) {
        this.label = data().deepCopy(fields()[0].schema(), other.label);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.count)) {
        this.count = data().deepCopy(fields()[1].schema(), other.count);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'label' field */
    public java.lang.CharSequence getLabel() {
      return label;
    }
    
    /** Sets the value of the 'label' field */
    public org.wikipedia.miner.extract.model.struct.LabelCount.Builder setLabel(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.label = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'label' field has been set */
    public boolean hasLabel() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'label' field */
    public org.wikipedia.miner.extract.model.struct.LabelCount.Builder clearLabel() {
      label = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'count' field */
    public java.lang.Integer getCount() {
      return count;
    }
    
    /** Sets the value of the 'count' field */
    public org.wikipedia.miner.extract.model.struct.LabelCount.Builder setCount(int value) {
      validate(fields()[1], value);
      this.count = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'count' field has been set */
    public boolean hasCount() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'count' field */
    public org.wikipedia.miner.extract.model.struct.LabelCount.Builder clearCount() {
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public LabelCount build() {
      try {
        LabelCount record = new LabelCount();
        record.label = fieldSetFlags()[0] ? this.label : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.count = fieldSetFlags()[1] ? this.count : (java.lang.Integer) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}