package fileopt

import (
	"os"
	"path/filepath"
)

func FileExists(file string) bool {
	_, err := os.Stat(file)
	return err == nil
}

func GetExecutableDir() (string, error) {
	execPath, err := os.Executable()
	if err != nil {
		return "", err
	}
	return filepath.Abs(filepath.Dir(execPath))
}
