package klog

import (
	"fmt"
)

type Module string

func Info(m Module, desc...interface{}) {
	errs<-AppErr{m, false, fmt.Sprint(desc...), nil,}
}
func Warning(m Module, desc string, err error) {
	errs<-AppErr{m, false, "WARNING: " + desc, err,}
}

func Fatal(m Module, desc string, err error) {
	errs<-AppErr{m, true, desc, err,}
}

type AppErr struct {
	module Module
	fatal bool
	desc string
	err error
}
func (a AppErr) Error() string {
	if a.fatal {
		a.desc = "FATAL: " + a.desc
	}
	return fmt.Sprint("In ", a.module, ": ", a.desc, " caused ", a.err)
}

func purgeErrs(errs <-chan AppErr) {
	for x := range errs {
		if x.fatal {
			panic(x)
		}
		
		//TODO: Better err handling
		fmt.Println(x)
	}
}

var errs = func()chan AppErr {
	r := make(chan AppErr, 10)
	
	go purgeErrs(r)
	
	return r
}()
