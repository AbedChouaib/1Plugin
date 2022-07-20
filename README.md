# 1Plugin

## Overview:

The 1 plugin is a Java-written software that utilizes Fiji app, this plugin uses GPU-accelerated computation to speed up intensive operations. 
For optimal performance, we recommend NVidia GPUs. AMD Radeon series may function, but are unstable. Intel GPUs do not work with this plugin. 
We ran GPU/driver compatibility tests and concluded that NVidia GPUs produced before 2017 are compatible with 1 plugin. However, GPU driver version-related issues may still occur. Furthermore, we tested newer GPUs produced after 2017. NVidia Quadro RTX 6000 (launched August 2018) GPU running on a computer equipped with a Windows 10 pro operating system worked well after changing the GPU driver to an older version, namely 431.02-whql June 10, 2019 (versions from 419.17 until Cancel changes431.02 would work). Another newer GPU that successfully ran 1 plugin was NVidia GTX 1660 Ti (launched February 2018). 
However, we had an issue with the latest driver versions installed (512.77-whql May 12, 2022) that triggered an Aparapi OpenCL error in the ImageJ log window. This error is due to NanoJ-SRRF using the old Aparapi framework. Aparapi is a framework that converts Java bytecode to OpenCL at runtime and executes it on the GPU. To avoid these issues on other devices, we recommend installing OpenCL and OpenGL compatibility packs for Microsoft Windows 10 to fix the bug and make the GPUs work correctly. We found one GPU from 2020 with which we were unable to run the 1 plugin, namely the NVidia RTX 3070 GPU.

## Installation instructions: 

To install and run this plugin, the user must install all related software using the links below. 
> Fiji: https://imagej.net/software/fiji/downloads 
> 1Plugin:  https://github.com/AbedChouaib/1Plugin 
NanoJ-SRRF: https://github.com/HenriquesLab/NanoJ-SRRF 
NanoJ-Core: https://github.com/HenriquesLab/NanoJ-Core 
NanoJ-SQUIRREL: https://github.com/HenriquesLab/NanoJ-SQUIRREL 
NanoJ-eSRRF : https://github.com/HenriquesLab/NanoJ-eSRRF 
Image Stabilizer: https://www.cs.cmu.edu/~kangli/code/Image_Stabilizer.html 
Microsoft OpenCL and OpenGL Windows 10 Compatibility Pack: https://apps.microsoft.com/store/detail/opencl%E2%84%A2-and-opengl%C2%AE-compatibility-pack/9NQPSL29BFFF?hl=en-us&gl=US 
