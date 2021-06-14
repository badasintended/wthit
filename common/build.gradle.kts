architectury {
    common(false)
    injectInjectables = false
}

dependencies {
    modCompileOnly("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")
}
