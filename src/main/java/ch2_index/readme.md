## ch2 index
### major changes
- RAMDirectory is deprecated, replace it to ByteBufferDirectory
- IndexWriter.optimize is deprecated. So the testcase related to it is removed.
- APIs related to Field index, term vector, norm were all deprecated. They are replaced with classes extends Field class.
- IndexWriter.setMaxFieldLength is deprecated. It is relaced with LimitTokenCountAnalyzer.