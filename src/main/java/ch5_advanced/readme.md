## ch 5. advanced search
### 5.2. Sort
- setDefaultFieldSortScoring is deprecated.
- In order to use sorting by field, SortedDocValuesField should be used when indexing instead of normal StringField or TextField.

### 5.5. SpanQuery
- query.getSpans is deprecated. Replace it with recent style codes.
~~~
        Spans spans = query
                .createWeight(searcher, ScoreMode.COMPLETE_NO_SCORES, 1f)
                .getSpans(searcher.getIndexReader().leaves().get(0), SpanWeight.Postings.POSITIONS);
~~~

### 5.6. Search Filter
- [lucene document](https://lucene.apache.org/core/5_4_0/core/deprecated-list.html)
- search.filter is deprecated. Official document recommends to use query instead of filter. So example codes from ch 5.6. are skipped. 

### 5.7. Function Query
- CustomScoreQuery is deprecated. Replace it with FunctionScoreQuery.
- FieldCache is gone. Replace``d it with SortedDovValuesField.

### 5.8. Multi Searcher
- MultiSearcher is deprecated. Replaced it with MultiReader.
