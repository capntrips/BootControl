cmake_minimum_required(VERSION 3.22.1)

project("bootcontrol")

add_library(bootcontrol SHARED libbootcontrol.cpp)

target_include_directories(bootcontrol PUBLIC include)

find_library(log-lib log)
target_link_libraries(bootcontrol ${log-lib})
