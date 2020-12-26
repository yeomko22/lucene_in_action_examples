## ch3. Search
### major changes
- IndexWriter.getReader() is deprecated. So NearRealTimeTest.java is removed.
- NumericRangeQuery is deprecated. Replace it with IntPoint field.
- BooleanQuery Builder