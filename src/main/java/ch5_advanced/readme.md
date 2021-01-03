## ch 5. advanced search
### sort
- setDefaultFieldSortScoring is deprecated.
- In order to use sorting by field, SortedDocValuesField should be used when indexing instead of normal StringField or TextField.

### SpanQuery
- query.getSpans is deprecated. Replace it with recent style codes.
~~~
        Spans spans = query
                .createWeight(searcher, ScoreMode.COMPLETE_NO_SCORES, 1f)
                .getSpans(searcher.getIndexReader().leaves().get(0), SpanWeight.Postings.POSITIONS);
~~~