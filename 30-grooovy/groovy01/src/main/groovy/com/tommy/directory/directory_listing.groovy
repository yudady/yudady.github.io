package com.tommy.directory

dh = new File('.')
dh.eachFile {
	println(it)
}