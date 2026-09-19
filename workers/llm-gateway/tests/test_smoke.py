def test_imports():
    """The llm_gateway package is installed in the workspace venv and importable."""
    import llm_gateway

    assert llm_gateway is not None
