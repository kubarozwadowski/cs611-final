SRC_DIR := src
OUT_DIR := out
LIB_DIR := lib
JAVA_FILES := $(shell find $(SRC_DIR) -name '*.java')
CLASSPATH := $(OUT_DIR):$(LIB_DIR)/*

.PHONY: build run clean

build:
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) -cp $(SRC_DIR):$(LIB_DIR)/* -sourcepath $(SRC_DIR) $(JAVA_FILES)

run: build
	java -cp $(CLASSPATH) Main

clean:
	find $(SRC_DIR) -name '*.class' -delete
	rm -rf $(OUT_DIR)
