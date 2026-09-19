package com.titleengine.domain;

/**
 * Where a fact or finding is grounded (HLD §3, §8, non-negotiable 2). Either a document region, a
 * transcript line, or a rule firing with its facts. There is no fourth kind.
 */
public sealed interface EvidencePointer
    permits DocumentEvidence, TranscriptEvidence, RuleEvidence {}
