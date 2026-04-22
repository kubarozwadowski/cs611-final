SRC_DIR := src
OUT_DIR := out
JAVA_FILES := $(shell find $(SRC_DIR) -name '*.java')

.PHONY: build run clean

build:
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) -cp $(SRC_DIR) -sourcepath $(SRC_DIR) $(JAVA_FILES)

run: build
	java -cp $(OUT_DIR) Main

clean:
	find $(SRC_DIR) -name '*.class' -delete
	rm -rf $(OUT_DIR)
