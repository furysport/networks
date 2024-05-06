<!-- generated file - do not edit -->
# 🔗 `{{ (datasource "genesis").chain_id }}`

![chain-id](https://img.shields.io/badge/chain%20id-{{ (datasource "genesis").chain_id | urlquery | strings.ReplaceAll "-" "--" }}-blue?style=for-the-badge)
[![stability-beta](https://img.shields.io/badge/stability-beta-33bbff.svg?style=for-the-badge)](https://github.com/mkenney/software-guides/blob/master/STABILITY-BADGES.md#beta)
![audience](https://img.shields.io/badge/audience-public-white.svg?style=for-the-badge)
![incentivized-✖️](https://img.shields.io/badge/incentivized-✖️-29220A.svg?style=for-the-badge)
![genesis-time](https://img.shields.io/badge/{{ "⏰" | urlquery }}%20genesis%20time-{{ (datasource "genesis").genesis_time | urlquery | strings.ReplaceAll "-" "--" }}-red?style=for-the-badge)

## Register in the Genesis

To register your validator node in the `genesis.json` you just need to provide a signed `gentx` with an initial delegation of `10000000000uknow` in a [⚖️ Register Validator issue](https://github.com/furysport/networks/issues).

The gentx generation can be done as follow (this is an example script, adapt it to your needs) with the [furyad](https://github.com/furysport/furya-chain/releases) binary matching the [network's version](/chains/praetoria-1/version.txt):

```sh
# Init node
furyad --home mynode init your-moniker --chain-id {{ (datasource "genesis").chain_id }}

# Create keys, be careful with the mnemonic 👀
furyad --home mynode keys add your-key-name

# Set account necessary balance
furyad --home mynode genesis add-genesis-account your-key-name 10000200000uknow
```

Then create your own genesis transaction (`gentx`). You will have to choose the following parameters for your validator: `commission-rate`, `commission-max-rate`, `commission-max-change-rate`, `min-self-delegation` (>=1), `website` (optional), `details` (optional), `identity` ([keybase](https://keybase.io) key hash, used to get validator logos in block explorers - optional), `security-contact` (email - optional).

```sh
# Create the gentx
furyad --home mynode genesis gentx your-key-name 10000000000uknow \
  --node-id $(furyad --home mynode tendermint show-node-id) \
  --chain-id furya-praetoria-1 \
  --commission-rate 0.05 \
  --commission-max-rate 0.2 \
  --commission-max-change-rate 0.01 \
  --min-self-delegation 1 \
  --website "https://foo.network" \
  --details "My validator" \
  --identity "6C36E7C076BFDCE4" \
  --security-contact "validator@foo.network"
```

## Genesis validators

<table>
  <tr>
    <th>Moniker</th>
    <th>Details</th>
    <th>Identity</th>
    <th>Site</th>
  </tr>
{{- $txs := (datasource "genesis") | jsonpath "$..messages[?(@.min_self_delegation)]" -}}
{{- range $key, $value := $txs }}
{{- $url := "" -}}
{{- if $value.description.website | strings.HasPrefix "http" -}}
{{- $url = $value.description.website -}}
{{- else if $value.description.website -}}
{{- $url = printf "%s%s" "https://" $value.description.website -}}
{{- end -}}
{{- $userInfo := $value.description.identity | index (datasource "usersInfo") }}
  <tr>
    <td><pre>{{ $value.description.moniker | html }}</pre></td>
    <td>{{ $value.description.details | html }}</td>
    <td>{{ if (and $value.description.identity $userInfo) }}
      <p align="center"><img width="80px" src="{{ $userInfo.keybase.picture_url }}"/></p>
      <a href="https://keybase.io/{{ $userInfo.keybase.username }}">{{ $value.description.identity }}</a>{{ end }}</td>
    <td>{{ if $url }}<a href="{{ $url }}">{{ $url }}</a>{{ end -}}
  </tr>
{{- end }}
</table>
