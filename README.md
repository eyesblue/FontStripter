# FontStripter
Script generator for FontForge, The tool help people strip unwanted glyph in font file by reading the reserved character present in exist file.

本工具從文字檔案中提取您要的字元，並產生FontForge執行用的命令稿，讓FontForge於字型檔案中移除您不要的字元，以刪減字型檔案大小。

The tool provide a GUI interface for choice files or files in folder that hold the wanted character, and generate the script for FontForge, then press [File]->[Execute Script] -> [FF] -> [Call] and choice the script that generate by the tool, the FontForge will strip the others you unwanted glyph in your opened font file, finally save the remain glyph to a new font file.

本工具提供一個圖形介面讓您選擇要參考的字元檔案或目錄下的所有字元檔案與目錄，並產生能讓FontForge所執行的命令稿，您可於FontForge中點選[File]->[Execute Script] -> [FF] -> [Call] 並選取輸出的命令稿，執行後將會從您所開啟的字型檔中去除其他您不要的字元，再由字型檔案內容產生字型檔即可完成自訂的字型檔案。
![alt tag](https://cloud.githubusercontent.com/assets/11750590/16642477/eb9757ac-443d-11e6-898e-7ae7f0dabe4a.png)
