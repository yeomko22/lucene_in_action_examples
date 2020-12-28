## ch4. analyzer
### major changes
- I Changed AnalyzeDemo to AnalyzeTest and gathered all the test code into this file. It seems more strict and simple than distributed main functions.
- StandardAnalyzer results are changed.
- When using token stream, reset() ans close() should be called.
- Genrating StopAnalyzer needs stop words set parameter.
- Building customAnalyer is changed. Related source codes are in StopAnalyzer2.java.
- PerFieldAnalyzerWrapper.addAnalyzer is deprecated. Analyzers for fields should be set during construction. 
- ChineseAnalyzer and SmartChineseAnalyzer are deprecated. Official document said StandardAnalyzer does same functionality.

### Skipped Parts
- keyword -> testPerFieldAnalyzer()
- NutchExample