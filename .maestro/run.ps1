# PowerShell runner for Berima Maestro flows.
#
# - Loads .maestro/.env so flows can use ${BERIMA_TEST_EMAIL} etc.
# - Generates a per-run BERIMA_RUN_ID timestamp for unique listing titles.
# - Funnels --env vars into Maestro CLI.
# - Drops screenshots and an HTML report into .maestro/output/<timestamp>/.
#
# Usage from repo root:
#   .\.maestro\run.ps1                          # full suite (flows/*.yaml)
#   .\.maestro\run.ps1 -Flow 00-smoke           # one flow by name
#   .\.maestro\run.ps1 -Flow flows-phase5/order-flow  # explicit path
#   .\.maestro\run.ps1 -NoOutput                # skip screenshot capture

[CmdletBinding()]
param(
    [string]$Flow,
    [switch]$NoOutput
)

$ErrorActionPreference = 'Stop'
$maestroDir = Split-Path -Parent $PSCommandPath
$repoRoot   = Split-Path -Parent $maestroDir

# --- Load .env ----------------------------------------------------------------
$envFile = Join-Path $maestroDir '.env'
$envVars = @{}
if (Test-Path -LiteralPath $envFile) {
    Get-Content -LiteralPath $envFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
            $key, $value = $line -split '=', 2
            $envVars[$key.Trim()] = $value.Trim()
        }
    }
} else {
    Write-Warning "No .env file found at $envFile. Authenticated flows will fail."
    Write-Warning "Copy .maestro/.env.example to .maestro/.env and fill in credentials."
}

# Auto-generate run ID if not set (used to make created listings unique).
if (-not $envVars.ContainsKey('BERIMA_RUN_ID') -or -not $envVars['BERIMA_RUN_ID']) {
    $envVars['BERIMA_RUN_ID'] = (Get-Date -Format 'yyyyMMdd-HHmmss')
}

# --- Build Maestro args -------------------------------------------------------
$envArgs = @()
foreach ($key in $envVars.Keys) {
    $envArgs += @('--env', "$key=$($envVars[$key])")
}

$target = if ($Flow) {
    if ($Flow.EndsWith('.yaml')) { Join-Path $maestroDir $Flow }
    elseif ($Flow.Contains('/') -or $Flow.Contains('\')) { Join-Path $maestroDir "$Flow.yaml" }
    else { Join-Path $maestroDir "flows/$Flow.yaml" }
} else {
    Join-Path $maestroDir 'flows'
}

if (-not (Test-Path -LiteralPath $target)) {
    Write-Error "Flow target not found: $target"
}

$outputArgs = @()
if (-not $NoOutput) {
    $stamp     = Get-Date -Format 'yyyyMMdd-HHmmss'
    $outputDir = Join-Path $maestroDir "output\$stamp"
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    $reportPath = Join-Path $outputDir 'report.html'
    $outputArgs = @('--format', 'html', '--output', $reportPath)
    Write-Host "Output: $outputDir" -ForegroundColor Cyan
}

Write-Host "Target: $target" -ForegroundColor Cyan
Write-Host "Run ID: $($envVars['BERIMA_RUN_ID'])" -ForegroundColor Cyan
Write-Host ''

# --- Invoke -------------------------------------------------------------------
& maestro test @envArgs @outputArgs $target
exit $LASTEXITCODE
