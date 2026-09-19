package com.titleengine.domain;

/**
 * What a finding attaches to: a link, a document, or a parcel (HLD §3). There is no fourth kind.
 */
public sealed interface FindingSubject permits LinkSubject, DocumentSubject, ParcelSubject {}
