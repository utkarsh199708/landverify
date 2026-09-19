def test_imports():
    """The docproc package is installed in the workspace venv and importable."""
    import docproc

    assert docproc is not None
