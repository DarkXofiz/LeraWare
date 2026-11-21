#!/usr/bin/env python3
"""Builds mappings/identity-named.jar: intermediary -> named, where named == intermediary.
Lets Loom compile sources that already use class_xxx/method_xxx names, unchanged.
Usage: python3 tools/make_identity_mappings.py <minecraft_version>"""
import io, os, re, sys, urllib.request, zipfile

mc = sys.argv[1]
url = f"https://maven.fabricmc.net/net/fabricmc/intermediary/{mc}/intermediary-{mc}-v2.jar"
jar = zipfile.ZipFile(io.BytesIO(urllib.request.urlopen(url).read()))
lines = jar.read("mappings/mappings.tiny").decode().splitlines()

cmap = {}
for l in lines[1:]:
    if l.startswith("c\t"):
        p = l.split("\t")
        cmap[p[1]] = p[2]

desc_re = re.compile(r"L([^;]+);")
def fix(d):
    return desc_re.sub(lambda m: "L" + cmap.get(m.group(1), m.group(1)) + ";", d)

out = ["tiny\t2\t0\tintermediary\tnamed"]
for l in lines[1:]:
    p = l.split("\t")
    if l.startswith("c\t"):
        out.append(f"c\t{p[2]}\t{p[2]}")
    elif l.startswith("\tm\t") or l.startswith("\tf\t"):
        out.append(f"\t{p[1]}\t{fix(p[2])}\t{p[4]}\t{p[4]}")

os.makedirs("mappings", exist_ok=True)
with zipfile.ZipFile("mappings/identity-named.jar", "w", zipfile.ZIP_DEFLATED) as z:
    z.writestr("mappings/mappings.tiny", "\n".join(out) + "\n")
print("OK:", len(cmap), "classes")
