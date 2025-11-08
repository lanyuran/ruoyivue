param(
    [string]$Root = (Join-Path "P:/Projects/ruoyivue" 'ruoyi-ui/src/views')
)

$pattern = '<(?<tag>(?:el-)?button)\b[^>]*>(?:\s*<[^>]+>)*\s*\s*(?:</[^>]+>\s*)*</\k<tag>>'

Get-ChildItem -Path $Root -Filter '*.vue' -Recurse | ForEach-Object {
    $content = Get-Content -LiteralPath $_.FullName -Raw  # `cat` alias works too
    $matches = [regex]::Matches($content, $pattern, 'IgnoreCase')

    foreach ($m in $matches) {
        $line = [regex]::Split($content.Substring(0, $m.Index), "`r?`n").Count
        "`n$($_.FullName):$line"
        ($m.Value.Trim())
    }
}
