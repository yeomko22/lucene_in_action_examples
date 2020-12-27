## ch4. analyzer
### major changes
- I Changed AnalyzeDemo to AnalyzeTest and gathered all the test code into this file. It seems more strict and simple than distributed main functions.
- StandardAnalyzer results are changed.
- When using token stream, reset() ans close() should be called.
- Genrating StopAnalyzer needs stop words set parameter.
- Building customAnalyer is changed. Related source codes are in StopAnalyzer2.java.