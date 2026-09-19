package com.titleengine.domain;

/** A money amount in integer paise (INR implied); never a float (CLAUDE.md Money convention). */
public record Money(long paise) {}
