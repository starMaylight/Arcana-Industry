package com.starmaylight.arcana_industry.client;

import com.starmaylight.arcana_industry.guide.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Reader for the server guide snapshot. Contents drill down domain (entry category) -> chapter (tier)
 * -> device group -> entry title; picking a title opens it. Entries are kept in that book order, so the
 * bottom bar (children 0-3), which steps entries and pages, walks the book and opens the reading view.
 */
public final class GuideScreen extends Screen {
    private enum View { DOMAINS, CHAPTERS, GROUPS, ENTRIES, READING }
    private static final Comparator<String> NATURAL = GuideScreen::compareNatural;
    // Exact shipped prose only: a datapack replacement at this ID remains the server's text.
    private static final List<String> CRUSHER_PAGE_DIGESTS = List.of(
        "2a8d98df50fd6dc854cebed82915f742eec64537821b34b9d97aa337225a0af9",
        "efe1cd3808ab5c13560df30dd44f4bd5df27e488e8ad959f6f07113a2a0f66f4",
        "096dfda67988cf119b8766a50ee679960253b1341e9eb123b5e4c95eff429210",
        "f6e7c480d386ea0b0edb9bae8fa083e5856adcf08391de57dbd9e8e75a6866c2",
        "5e291ca0c88c35cdcfd60a464888bb19d89485aac0daa3da27b3f09d8e19ebf7",
        "fd9786369bdd3971d9b3d47744a737a206be8d548089ad4239dd95df20ad2f03");
    private static final List<String> KILN_PAGE_DIGESTS = List.of(
        "108de8450d527ac612d6ad182cc748975f101645b465d33331fe5e8dccf208bb",
        "2fc5aea8d8ad0eb1dcf12d2bb60aa7a793bb4397afab8c15fb5a7cbb746a6b91",
        "d060d52e361e6b9c9900348ec6f9301598c2b69ffb642b623af7daf4c2241bbf",
        "37fd3081c05e09d0c8326d0068f29ea1a531b9f8c142f57847cc8c542a8f9a8c",
        "831bd402d81918b7739cb3bf1a115a9d6c785dedc17ce356a1449d0376a93533",
        "1e9c3a009e819eeb2a96afaf1143454f270979e21026b7d67106aa099bb80452");
    private static final List<String> SIEVE_PAGE_DIGESTS = List.of(
        "97e1ae923fb02a2a91b7e445c0f592b9f24313f47f8d481e241542b628ec5564",
        "4b2fd893759580aca98ad3fd238a5d43534210e7a397ab7e92f1ad259f2daae8",
        "c21d50705d4d4af63ec7a03221b96915191459d8187e063d4882e5116ab93f95",
        "907a9b6a8c407728d02b0697b602517583684521f349d07ade5831c12de78ece",
        "149d6fe0fbe880e0717a3c742b840068398166c138ff38bad326782df64deb49",
        "5af7ad4c00eaf0daf4942bcb28aaaa4d5470c1e60732bffeb48df2169fcccc72");
    private static final List<String> FIBER_CARDER_PAGE_DIGESTS = List.of(
        "e2ce511bc98c39a6f331af618e15db30a841b7ce6824c99e668b31561d96d2f5", "61e052fc86546a2f39308489ba833c1e0e065f3012a7e472a64678ae09edefed", "6d9c4056e410552fba23dcf50bcda22fea2f507fd77edd6b22dbb29ff65e115f", "c77247f19759ec36e8d02669888013594fc3e59e97537d9581f5c0fdaef211e0", "f635c133c99af11cf293b75d336f63c813618ec9195fb9c3b4aa41a0f61d87d2");
    private static final List<String> MOLD_CASTER_PAGE_DIGESTS = List.of(
        "0a81fc9ef0b8dd708d4d62601fbd74767f36a9fff4677a64a2a804896a2fc699",
        "015932af533b8859659c5c0f34fd4d4f1714a7502c9efda8ed26d3ebf22a6bb1",
        "2edd8dd0d60469d04101b46d50374e574ada828a55a6d3a55ac9d3ca180c7041",
        "ad1b114a5c02e254f34c1d892b77f2fb3f726a28aaa6e7f574caf5c77916256a",
        "aa896759c1710bd9c946710ef9741fb6649ddd056b010df096b0de6f34cfce5d",
        "5dbc341786d7ec847b99b2ab6ad561e055c7e92e9e32800c8c4b0676c1092bf2",
        "4a054382d2c2ce54f98e775e63983db165bf4f80148fca30cfe1377f44c60f47");
    private static final List<String> SAWMILL_PAGE_DIGESTS = List.of(
        "0ce766d8a169ed827b960e55e9c225965395c6b0019edce35ac7ebd48ed58578",
        "1b9a005ce6a244ad6575ac272f846e08913b09284f9c6281e2c044064ad2f091",
        "3ed6a48799813d148ef0cdfbab265cba951f1c964d4b962ed016273560d8b457",
        "f762784178a67f38f773d78894f77819f137e4ba2041543d29dcf8d46b631b0f",
        "9f2edbce4a226914281a9ef8f41a1295cc8d1d965e3f648216e0ed820fb1cdce",
        "f55b2a5181a5bb312b9d9b8d54799df5cd2c8ae37843f23976eaec1fe13bc89c",
        "8d0d50942d3d0a1df17264e5a0c4066cd00917d350387b0c7bb3fe17328998a6",
        "6091433dc37e7407e3c9c96b134c37a5b90392ece510a5224f08aaae5bbfd302",
        "a14c775f4546aa452c8e162d6558896f3599a7bae93d8208b5553ba005329914",
        "4aa78b0a2b4401eedbd83334f08819c350615013554e29380aa58e33fe3644f3",
        "6e4438e9a3a10da1ec4936344c735d8b372b71e349bade767760558456145247");
    private static final List<String> REFRACTORY_MIXER_PAGE_DIGESTS = List.of(
        "6948e9529f07aa494be4320ebd6fdf9e08498dc9600260795c68c4f24144a59f",
        "5e382c7e7c9cda65621f99cb33089cc287048da0bb9c007be5dbfad05aa44278",
        "df321a2f88e60d883af0d45d8a9a57ce5fda448054f8c2b49247d1186489f2d1",
        "3cc02d32e718e4d38acab8d1c65a388e6ea204ae17be57c5ca67910203dce712",
        "87465eb461bd7a98a4c7e5269b50da772962df97711cf0057925b85b80256125",
        "94e105f5bb1b1dfc6e8d993f956cfbc976c091e42b3bc5e6b240af287f34565c");
    private static final List<String> LEACHING_VAT_PAGE_DIGESTS = List.of(
        "14e7d058ee8f0e79a5e1de468a75db47f87a79f66ef51c0e98ed8853f18e1699",
        "db507b4a08a03a20b4eaf9acd5db1cd0bfeeee33a3f537df91c80fd6de1c0af8",
        "4199eee14f0a69752a0da3fd34fabf9b6fae05a65c10f49683f5e3818b23805d",
        "feffaec273ae9aac01719d0bcc99334dd8945501af2f970de5f89c1acc673cbe",
        "d3c56bf71f25e7f83ad6d2a0a9b331c08af9df0a6870d98885164f86ea1d7355",
        "cdb403682003621b8e482b72d03d3cbd9e22eeb8a60124069f825e55abf9de4f");
    private static final List<String> ORE_SLUICE_PAGE_DIGESTS = List.of(
        "4c72c2dcc948479750391dbab541591d23cdcb3619954add8cef8b313078b3c5",
        "cfd86a5e7e39bdae5c8898f57840512fbca1b271dafc8a4c409fbf92ae14a007",
        "10530ad07cda396e4a13d782d1bd7bdecff3e923cc9f158e978fddb128a390cd",
        "a4b7fef59d3043d69e31eebfe7f0c022ce1a53b5f1066e0bda149230775b75f3");
    private static final List<String> COAL_FORGE_PAGE_DIGESTS = List.of(
        "099cc7310370d1ef8cff2c80771a64bcacac16cf477d2f29f1fe318fa8e83c77",
        "61d71f77e8f03902209b760accf526cd7c892693eda7eaa1c5c8789f0b156cf1",
        "2fc36833834b556089032cb696f16e5eb72d05f516f14ecde283b986f9e92141",
        "05d7e551e5619f3556f6894d0170db51e18d14ae8b0473d2fe456e90582ec5cc",
        "4632f2f0db99c90349a4ca8519648a194f9322764ee701805b7dc6d8e7190870");
    private static final List<String> CHAR_RETORT_PAGE_DIGESTS = List.of(
        "05829a05a3528dfee32d140a55d391fdf909e4b056be7d9acfff5fa7e35e5aa1",
        "5d04e540d6371090785f2861351bebc75d70b4fadce7ade05626076c5ad581cd",
        "1aa343e1be360bdf8f63f03c4e86d9a162481af4a5585b8d8db3521c80f6e8a0",
        "56fca21ff7a33ab5f1997cb03662cdf160d42ca86dc80be735ba9997247f1338");
    private static final List<String> ENAMEL_KILN_PAGE_DIGESTS = List.of(
        "acd0ffe9835851fad2ce7bbf505bb572b49f840a91ad94cc7115e6acfb4a4b23",
        "b71a07b0359c50100b801fca2b5d6bc6bcdbc72316af7a33217b6e57405df5e6",
        "018ed9c95c5c93b6481cbef7e2db1defe05a893855e407190d3348b73852f9b6",
        "776e5e460def28cb0f50993e31549b2ccec304485809745ff87de06095cdfe84",
        "c6df0fb63e8baa6e8d20eb523c26b22ea820f7647028f6cefa3ddd1e9a98518b");
    private static final List<String> HELIOSTAT_ETCHER_PAGE_DIGESTS = List.of(
        "fe806e7ae0a148c5711256119ea0fafe91efd85ed32699891fe35b2068ef7f0c",
        "cabe7f0db75ff95f96d45596ddd768254e5ff7b5f4b3c3b47988be05ade1418e",
        "35683b66ba76316c9f91f0568a8ce275264baa094f582028e0b54199de31e000",
        "98dd4d8d88bc75cf2d76976ea881b21b765051b9226222f0cdaddd206246cb32",
        "1bd8859df649a0bbcb8b5af224c4718f5973433367a9eed6e430f0287cdfccd3");
    private static final List<String> SOLAR_SMELTER_PAGE_DIGESTS = List.of(
        "6392285b3c54988b163d59a6cc8f80f6861d794fba0f133e6ed7ffe659de2f70",
        "87a2c8bb55b094e25541a94ea6e441b95e428bd4fc0103978d5afaf32c9f3777",
        "e8979115024135bb2d2b5e96ba5a1a8aafbc7fc983f0b2bbfbfbdd5d10bfdd3e",
        "ac64afad51d7216501347182d7631f68d93491064a89b20acf56db96db4ee3e0");
    private static final List<String> CORONA_COLLECTOR_PAGE_DIGESTS = List.of(
        "e58cc1f29cd9b5f3bcbca482d5fa2b630f8b3848d8284c2294fa05f5d92d354c",
        "79f9a0452c01ea215eda652880f04bd8fb79f4622965b95fd5f3824ef58620b7",
        "e7eb5251e6bf40c8e8ab5e618fae87afe4cc8c48c18848f3c530bee30aa7f450",
        "ae4388ae9672345ed076699455f0cf02feb1c6766307b2f3bfe07cf370783172",
        "98dd4d8d88bc75cf2d76976ea881b21b765051b9226222f0cdaddd206246cb32");
    private static final List<String> NOCTILUCENT_CONDENSER_PAGE_DIGESTS = List.of(
        "9f16096dd4340106d28d5b8ae83889c970fb012421440210e85cfdafe0d6f12e",
        "c8bfe960e6d23268cffb2cdee20d5f7fd534d47748cf66305603b4c28b1f41fd",
        "582a60f1b3a17ef7d58bcf28e23ae9ed5089d537bddf9d87ed1e5c94361570b9",
        "98dd4d8d88bc75cf2d76976ea881b21b765051b9226222f0cdaddd206246cb32");
    private static final List<String> STELLAR_CRUCIBLE_PAGE_DIGESTS = List.of(
        "ae8ed306b644c115995e57c6a2ca826bd5ce00cb28df4dec446197bcf8421082",
        "2de67d96b017baad379a492c44223c2dedd71a0c1613fd852b3b0aebff47fed4",
        "3236b375783da4dd1106fe7a7d32950b55c353f3c0141d3970632db3d600b322",
        "75e3aa3760a277b2c1a1159b104c4cb13997cde973eecf4080d76a1f49c29f83");
    private static final List<String> ASTRAL_RELAY_PAGE_DIGESTS = List.of(
        "bdc9acbebbc3f626db061e727a0f73dc2f363800b8619dd3c39941a3a51c59df",
        "8212a2bd5edb6c38553cb606f6e6930d12be379eff63be83dfd2775221f34ff9",
        "daccb3a0d82e2050ad77daae970d214e496d2561849f0117ed3015175fa56fd2",
        "f14f82f4d674b394bb7ea82468232a1651580ebe0ff00555f0f26f5ec4a9d654",
        "d3632a176ca6bd53e28648e06fe6cee867ccc39a37dc474d15e00825b41f59d2",
        "57edacaf6e3cb8385e5b423b2cd28c68296ce30b8490be00621180973e16fd52");
    private static final List<String> LENS_GRINDER_PAGE_DIGESTS = List.of(
        "960db8f80f3d44ac8d731fc01c1e867b3bd7357e9aa50908af8d6dc567fdece7",
        "788ce45ff3d17bd7e5f7c76e94c375c2fb6b32d2ba5f4577810a162084463063",
        "e0c1daeb0a66a6503dff483c0b103311e182b5537aaaa6e861f558c3fb415086",
        "98dd4d8d88bc75cf2d76976ea881b21b765051b9226222f0cdaddd206246cb32");
    private static final List<String> PRISM_SCORER_PAGE_DIGESTS = List.of(
        "75109667ae9978311eefbb8f4ebac5fdbd4c7df7dfe5a8be9c3a43c88e954c8b",
        "2b7a1f3b49884c67f5fe76f1591cd901a066c81affc4b82d93d7ffcb7c539b7e",
        "574096db069c35ec76e39247beb896c0e9127f2303cfab0da4610554d177bde5",
        "de66c1bc78d0e20328ee603d1a533777195bc21299894543760382207e960620");
    private static final List<String> PRISM_POLISHER_PAGE_DIGESTS = List.of(
        "5491109dfafd10abed1255b8ba602f9799110fcbec6e2f1cbb87cb48dd16a55a",
        "9899582d9ed558b3322f65f8df51c3cfea8c28b7c5cef25f9a42998216e9aed9",
        "bff376a71b078aca5db9eabeaa78831a1495d99cc9e4be99b346b80b9c7a187b",
        "a348a37f03240f24359bc69b0ab7045ebc5fcd31012a9ef6357bd512ec01940d",
        "1949a073bcdfd1a38f2f858ea05e3d9f1a4f99b0d96d1ea90a8db81cc0192716");
    private static final List<String> PRISM_LAPIDARY_PAGE_DIGESTS = List.of(
        "78fafd009c1036a37bdcf53835496fb150f78ac296e11d315983b1aee44bf148",
        "1e31440b01497b945487f270a3a704d5fcff97c61fc1bae46dfd5bef8351baad",
        "0d7a727a54a775d5e999c89e49d8512a4889b9c4f4886a0db731bd241b849760",
        "b8b993a0366556bcf9c3490146f31e1bda893b03d686d29d6e6cb4358e736d1d");
    private static final List<String> PRISM_SEPARATOR_PAGE_DIGESTS = List.of(
        "5945688c61bc8dafc33982da86a2f9c282284d50bf74b7031038f1a484019940",
        "4b87561ca1cf9ed77a231ffcfe172e102987ea208ddc51e4f7f992e30fad2428",
        "376c59ec51db2eb8a1bbf7d8eb9fd2f72d2b7f6e64c4c6ec3e5c0f7463f1f23f",
        "755f4ec0f850537ebb1e367063ac8282e1e261177d64dd839279c89e503858fe",
        "ad743ade29b4a6a949c7ec1d7b039a1695ff608c9381b13a7971de34b583724c");
    private static final List<String> PRISM_TUMBLER_PAGE_DIGESTS = List.of(
        "cda0a555cbc40996f164e60a5b87d53f85295221d92530fd62d9553aa61bb840",
        "98131e0a51df93e9e733aabba2e3c46e4a63f2aadf1f02b71da3fd8254c97c1e",
        "08e92a5594e373cfe23236c1a0219bef250efd6cb99dd51f644083c31ae6e8a9",
        "c9496159805392597464e92db7855285fad79e598f19f2e3df59a868a0afaac2",
        "76f527ba950a9af969940223718cf61305bb87df9892ad2ce8a69641c4af5844");
    private static final List<String> PRISM_BURNISHER_PAGE_DIGESTS = List.of(
        "1d2e24452f7f152dac1b24fb11d21fe1cd2d5cb1b6bfdf4a40328d61cd40b0fc",
        "58cbc948b118e1387d4149d952ccb043914a101c5a644269388485a22645f822",
        "c2164cc701cfe88e491c98a36aa4927e394da61cf9e103d1ef0a86c36c599de4");
    private static final List<String> ARC_PRESS_PAGE_DIGESTS = List.of(
        "3f28bad5eaf7e86e73aca0b1fbee98b468919ae98bc137df58f97211702b199c",
        "4e79897ad8a70dc68ea9b30a9b4d668a22087b955316f68957bd6f1c79973aca",
        "023ad76efed6aa5d09dd960ba34945f240f287ba8f2f096afb106cae590ac861",
        "f6c65f3a6d1147eb135273d59654a95aedc1f769b5e7fc1534624e0712fcf1aa",
        "a6767c3a91b49b87cc5a984d85011dbdec18e2dda4bfa487630cef57ddd3872c",
        "8de9bdc7fba70f7a3a0243a780f493e92341c92c6585c79446246c1898650a5f");
    private static final List<String> BROACH_PRESS_PAGE_DIGESTS = List.of(
        "ecb136889d9af5cab7429a6a30998ecb2af36469bf1aa1a1b94fd969bfbdd7b6",
        "05d43d31b514b69dc06efc2c674fa7ff94a3b2aad004ce97d517e676ceab9d3a",
        "5986605b1dcd691031aac6ac8c7885a9e32dc001eecdddf120de14fac03561b3",
        "51866cbde48e6612b21979027cbd7a9b02e62f3089b3c74d5346a32998ba917b",
        "86221ef68e3cac2dd72fc0f795f7726fcf0a986cc0f1cb869073e4bbf22472d9",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> ASSAY_HEARTH_PAGE_DIGESTS = List.of(
        "cfaa00402503c61868275ef30fc5ba5137d016b0cae5c8f23c19ebf714058acb",
        "8140c4a6e4898c261021b3f2b7bc74339b34d336d4e2d07ba2d375d744ae1712",
        "d45931c988408c848968eac76b38196990ae95684b5ffd38e5c768e3478eb419",
        "f8e8527bb12df53188dba460bcd0a18285451ba8f1f31a8de7f2697f4bea8ea3");
    private static final List<String> DRAWING_BENCH_PAGE_DIGESTS = List.of(
        "1d1829d847137e55056b43031be36bbcd3282ba9dad4fbceeeef923089cc855b",
        "e1435cee25704ed7cea15015c5cc70e223555660f1ccccc3502c1efb48616f60",
        "77f0e07282d976e00df38f25bf895a3cd91ec072f721d6fa1098abf377ab380d",
        "49af1fe26a2cd9ca724d812a8020c0851f1174e5dd7de9f1c327cf0d3fb07494",
        "ffcc86595beb62c506f7878f5b19653c9c5aa84a91dd88c571c72fa2d7fef3c8");
    private static final List<String> INDUCTION_COILER_PAGE_DIGESTS = List.of(
        "07fda03e3d6eca8563f03dccb2c8f0222b52821b0bbbb97d112d35f15d2c355e",
        "dae08288d340c51e39caf135b8f3a2e6e9823f9d4b97ee352cd5563202d235dd",
        "7e785963b34d4cb917e8993fbf5915d787177e16911dc82117670a5894c6efbe",
        "5a577054e2a474aaaf00b41b83bec0b830d9e27cf3b66bc35a71f52183cb7109",
        "8de9bdc7fba70f7a3a0243a780f493e92341c92c6585c79446246c1898650a5f");
    private static final List<String> CAPACITOR_ARRAY_PAGE_DIGESTS = List.of(
        "d87bfad8672b189dd925a809d45493197f1a72ea7906e29d74324c500886d643",
        "66bab388e7511b37370f22a10655d6c5140cf0b1eed826df12609d15db38b88d",
        "ae1c1b69e157ffd0423194ffc3eb78d974d034bfb1ae6e12ace5330e19eb7216",
        "fc69fa7cf06045a51f1916b6eb789b2db3a4f1040a2ffa9bba0c213930f01d41",
        "f9aa53ea231f4804ed5e44babbb4a2beaa5726f2bf92de3bf383b8eb8ff942c7",
        "9a2f47664aaa34fa730da1b4bba195131f122e2b4c09a0ae1f42768d1a87eb11",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> FLUX_RECTIFIER_PAGE_DIGESTS = List.of(
        "e1caa0dd0ddef163a846f7dd4c296b7ada394fc2be40ed2525220b60679e9686",
        "747be99a535f7aef305d8db2f567e19b0638e91b3e7d8b9ce428204af3a2b51e",
        "c1cc9f52f161415d8c7c295d9eced4860f9d1a15f815ee6f5029ab163414c8f2",
        "90c4c588f4972dc320c62ac72e4b6f06d8b053a418504bf05630c2667a2f50bd",
        "12e862d02cfcd998ae2907015c19134c76db4714a6d9acb6d1409a4f8e1eaf2d",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> POLARITY_INVERTER_PAGE_DIGESTS = List.of(
        "55a21cc4ec175a1b8ccc710fed07a8793c478f04b765b7f8c1c7a80215c41bc5",
        "be255fde54f814b16f766fec73cb53be1c09a570117bc29dac95cd19664961cb",
        "dd0b2e5b6c728e977bbbfdd55fcb8b95d2f6383671d293cec0a78ec5a25a3315",
        "f0b4b86e1d84eb3adada0b46e0b9732395cf6f198f4027e9ca31c02cfcdb6014",
        "e1941278597e598e0acc870ec8c5cf9557ecd6a115544e18442afb55c926aab4",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> DEGAUSS_COIL_PAGE_DIGESTS = List.of(
        "4be9d3ce8a2a3aebe89e950fce18c1ccd4a93dccc5fae5db4849b7dbd08396cf",
        "3c9928cc0aaba4283297375876eb214325dda3d410bde43f822107b7618fa325",
        "38eea2bb989510f22cc8ad411fe48c2068cdf7566dde72da6feaa3ee90464c4b",
        "0b946f21d87982321e0cfc957b2e007c3b4ecd7127abec8686a9d1fe670e8251",
        "06d3ee1d3bb0423822e832227164829ec5e76f82be6ca2704e91f90affa8ad31",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> ARMOR_DELAMINATOR_PAGE_DIGESTS = List.of(
        "4a528637334e8209835c86056f3b37c610782294878f1c931adac6aa3f103f7e",
        "7dd95fd5b89852a6ddd45af1874b3fa91b90b5afab72e6d89b8c0c64593bc806",
        "94b5abfbc4ce036678b8370e13f3d4e9de23464c76e615be9ca110f206d21fe0",
        "41848634846b0f6c88f9c0cc0414e55772c2358db8913f3ca3aa13d9ad7303c9",
        "59c0ddd87346aa78a1d9954aacdd064de42c770ac1d608ab4c839267e7ab7cdb",
        "0da5c8250d354ed0a2f70fe55852db8d63f24ced2d8fde543bcd268ce0acf800",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> SIGNAL_IMPRINTER_PAGE_DIGESTS = List.of(
        "b49d7918ca671511d134351d677d5efc7ab5f1a33a92cc444419410df67fa795",
        "9276270d23c7a4158f95a97f7727726b243d9ec586131e66bd4c05fa3bbbbc86",
        "c3e815ad9838865cea538d56d808b7572ea2a72cd4042e1ecf10555ded23b087",
        "af52ada88013820f68c267a66a307600cb873cdc8697774e307b152cea1ed829",
        "a4b0302127c28777f2958cfe932640411774a75da99eef37000ac0c1b53ee776",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> RESONANT_CULTIVATOR_PAGE_DIGESTS = List.of(
        "236f9f77721af6b8aca00cab4b8282e0485ae0ad764f1c9cb1b00533654d9a8d",
        "00f86a4bf0827c59fa64894a799cba804b17c83626d9f2b63337e072489ef0c6",
        "cda7ca72d7bcbaff55e38a2c9c684d900616d1c1f09e8c2e789022fb8bd43e70",
        "0b995fae079ff2b98d6ff9c74e0f9bb1db65815e22fda113866a4d97f68fef50");
    private static final List<String> VERDANT_DISTILLER_PAGE_DIGESTS = List.of(
        "52d1777cc68ff98875701d23d9b479cedf378ac3203766ec03dfb5fcabd7b469",
        "669317a04196c89593b9ef5afa422bd381a871137cbc8aaf6444df1e0e4c4b55",
        "1469259a583d56d54d30f673342c4eafab83a050ea96f4ecd03156b19157a493",
        "49070936a1a69880956a383399b645ac85a211d33997d637b8982850e5470ddc");
    private static final List<String> CHROMATIC_KILN_PAGE_DIGESTS = List.of(
        "036ef690127a547046cf7c5b510b4d4630615071073e1ab016266d942713f453",
        "d98d769aad892f41ede36c54c08f2a3b0be8f9eb1f0a8b485de0e46f236fd55e",
        "9e9a8ce2114cde590b5a439683c59e5fb29786eb2c359b49226508e3c03840fd",
        "90258053e71933ae13127665225c19531532151a19d4fe1b6ca0419fa150375a",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> PRISMATIC_PRESS_PAGE_DIGESTS = List.of(
        "595d9ecc346fd83ecf1aa8182081afb602f8e01d01abad538193f766e7b79b42",
        "510c9c1936ac24fc15ba03fbbaf9f7fb26d34470f7c6ff1814103458cde67479",
        "b2cc124484c1fcb040aa9caf35ccf830328eaa2b1678c802de375e217e33a46b",
        "bf487de6614c84eb47dbfe391b835931e09f61db1d3c51414e009fd9d497dc9e");
    private static final List<String> ELECTROLYZER_PAGE_DIGESTS = List.of(
        "8a086a02ef1f4b14c7d123d203ef4b6b2b890dda5ae4b1c954a6385f098876ba",
        "a10f743f2a57a4d2191406cc06d4ed427e0093a304cd1502aa63a6016948946c",
        "a45f95561b74f7c9faa4b56a67d87c0ee19a5ed13c0158135fa88c1746942d2e",
        "d7d42d0a2e1a6e621e35cd3cc5096a09b552c23820af423bbac39cfdf25f6889");
    private static final List<String> MAGNETIZER_PAGE_DIGESTS = List.of(
        "5b7f5d15ddc205ea3de4b34d9a1f7516ecee6a6a31eb24ed78fbec6dc649d29f",
        "b2c666da721dac10aa7f395e4f0c5a97ecb2327f4379e5b596c06e3daa0563a7",
        "b2213c0b4b536fe630cba994abcbcae6b7b112f45d19e8ad5688189bd677b898",
        "24572b18a9fd7c18ee079d662720262be96d04b779cc3d86338a0755f0e690bc",
        "da912d70ea485f4f111e15ad66797a747652ac6218e19bad28b1f401ae3c6ce2",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> CLAMPING_PRESS_PAGE_DIGESTS = List.of(
        "3971c442b31c83ed7d8e7354f2c84791d3af193943a6482020e3adc913ad3cfa",
        "1e432df65972bd194a041c5f2694d1729efabfa3fe17f29f055b74eb5cf3df76",
        "e91e91bb75b3b2c686532fd964fb147e364171c834c74d1972bda768a40340f9",
        "b16b52c250afbbb73eaae68318d25a9e869899aba8e3b7a9ae1451f22244a62e",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> QUENCH_VAT_PAGE_DIGESTS = List.of(
        "6c0b5e2511ef89789edadd90ba1ae33609fc0a0557d9ca8c930ff299cd164245",
        "d95b5fc595d6ea76ef4de72f10d472f0e13e2063f64ddc5851f784012378284e",
        "71c0bf19eaadfe4dff0f2f7983a8fdd26664538ecb328d024edd932ad338d4cc",
        "2ac89089a162f8bf295d000f34942c4c6dae51c668cb5d94a40f0c009c4339ec",
        "ccf7d2a1f6e49c54c390f9adb2f7cb3e553ea9b88f6e27035164f6db4092a36d",
        "81251d63bca55916851883e24407ead3929748b44e8bf700a21e7b2220265eed");
    private static final List<String> PRESS_PAGE_DIGESTS = List.of(
        "d22b761e9da974a28fdcca3e7660f71e9943380fbfc0c7dbaf4b96ac3fe05147",
        "5e94085507f531a1889076e6a62a256046d3de8fca8ea067e384993cc98f9081",
        "67bf9634db231dc61f066b3b5b23b2027a2fd0d27ebd4add2d904108c7eb958c",
        "0ba477086966745833e038ffe5824e4a6718d34e9dd494c135e8ce986ba8f951",
        "8262fd9ed58c2a3cc820d2deadfd1ab5f632d62b23533f58be27e9f0f6f1c07b",
        "84847f0ad4953eeb293f863ce0babeec53e7b04ab82a5a7831748fd43d84d5ae",
        "809a32ee53b5cc7d63aa9bf38b492f88ff2a89a533b6e94faf12106b764bc8de",
        "41a7a844d1189a9f3627c8b955583b99edb8bb8f93147c29267e5e669d223348",
        "ad4bfaf915cc4d2b8c62c0f89f48cc911bcf9f09887d86e6ae460cc21e6311d8");
    private static final List<String> SWAGE_PRESS_PAGE_DIGESTS = List.of(
        "e691295a571aa0f451d94e39b88d034c76733d2e9f9700d1a7c2e0685c806612",
        "c8806ea5bf5c6f820b513ffaea62ea8a11b1dd0eb5ab91707bc553c2629d2cc7",
        "52590d3706297dacd16fcc9246385de0b3c3227ed62bd8eac28706d5b8fd6b48",
        "58e52d01431c48202be6b6bc6974e4a2eea47ec00c06e470a07b0e7078807a06",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> CRIMP_PRESS_PAGE_DIGESTS = List.of(
        "b050aa0d850855b789e9cd11eccc59d492e7fbe8102f71e6fffae25158812416",
        "bc2c3db0e420c8cee7104cdc4df6cad511ece29775157d87faa4b66078551141",
        "6c72925505100376f9672cd9ddb49a89a8a6df41eada13098ec49a3a82127fd6",
        "8c1e5aef86c69e261a4b02c1fb8b53d189420fcbb37e45b8621987742280608c",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> FLANGE_PRESS_PAGE_DIGESTS = List.of(
        "9677b37c48f3355c7c53979c4ab83b0dd428550d7a84e441b3a63097298a9d74",
        "66d8cba02266ed54c27c52ed8e9b71773d4acdcfe4309e9e798d986ec84c265f",
        "209c97d1a458a512ebff6f199c7af1f67c4b33edc041e95693bd85f44430fe4e",
        "89cf480a1cb71076d4b8455b4bdf14001acd08a269922c7ace5a718e16eb3cf8",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> SEAM_PRESS_PAGE_DIGESTS = List.of(
        "73b6b0373a810660e587ca1e53ba3396a286a11fdfa38a2454c8840d8c9d5220",
        "9694cad67ce186d81815e05bf2b26319007a22f88ac050e75dbe4f9b4498eee9",
        "c346f92ead30507927842b5ff5cb8257b66446d0f3c416981494f7fc6b13f882",
        "2e6fe91974f7a7ba790a613ca7b350ac2846f883302d811b4cc868b28d74d341",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> COPPER_HOT_PRESS_PAGE_DIGESTS = List.of(
        "ec95f3b8985b1ee40d3af4b8576f0fc9b41a8a581537240dc27c359a3875c207",
        "961b554a002c676579073be7990401e2b1d402919520a0f930bd5053ae8b7c59",
        "662b807c6a5e6fbd72901afb14f563d00169b450a78e4470d2b4a619bd9539bb",
        "af2274cf009a2d3231119ade5d3331b62a14e4514f284eee1edec6efec497162",
        "e8a0d8401ca2a4fad36ed5d0a30be2ce0a2901b77d4243caa9695c556d83676b");
    private static final List<String> MILLING_GANTRY_PAGE_DIGESTS = List.of(
        "a89aa6936da3ec4a6e15d1cae07f040cd192fafd1c7a542a8ca1ebb52e5293d3",
        "c28406a9a13e83866214937dde7231885df72a1d3cd021769940da9535dd2e43",
        "1a605153383138764e3b3e1f6b2cbb79dcf158fce16636e15700bf1d5f31370c",
        "8ded4221060af18999a26919438928efe929267a243aa5df8a67e59564119bb9",
        "0417ebd31e34567725590474a52eda0bfaab755579ce41544d9d2127da8073b0",
        "58bf877546bd52c520f9f46fb0cd04c79f7938f3018787b7e1bcbcddff324f55",
        "ce887a47018840298481973218b9fb414c6f6589199b5d121ce3bc946a6a1455",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final List<String> CLAY_EXTRUDER_PAGE_DIGESTS = List.of(
        "f24d09850a122daa076aa8897bf9ffdc4d7679afecbf2afc95649a82d1ac639a",
        "da678f5b6c72a3ed0d72d4804d26428f5097e00eccc3669c0a247c4568eb4294",
        "b5620c2f095497301bf45fc01ab51380827fc0a10f086fa381ff4751c411c8ac",
        "ad47c9b2aedcf96aa6dac359a2185c85a125f7d315d8e2d8cf9d9b9cedcd09c8");
    private static final List<String> BOILER_TURBINE_PAGE_DIGESTS = List.of(
        "c38080a5745a9e7ae0f8edce84da0dc3d5eecb897f048fe1b35e1e00a48016d0",
        "d15fb49901539be6dbd93495f9196a76c1cf0ece350e92f1f3a4bfdaeb6cfb7d",
        "257c8d64470c845864ee6d565ac92e94ed65d0f117a04626787716d5f8fc6a04",
        "1b8d8fab8d27203346cd927284c0cae9b78c5d286afb764e1ee8400f878a72d3");
    private static final List<String> FIREBOX_DYNAMO_PAGE_DIGESTS = List.of(
        "6ab792009925dada92fe8be1ce2f8a41706414c0a7c89d4e1d87fe8289748554",
        "8289e663ad9b3eaf567d2a54ed204cb10706f589d1bed5dac0b4f8353fcd5d6f",
        "afdb106ce03d8e9264306fac25aaf3a4b02c0bc9ff994a5ba4d535db32ca1275",
        "d734ef4cd9122a97d4995f55259d1c896d2a3c8e63f788e29fc996b093318825",
        "60a1e1a9fec5056724ea744ef43112354d4ecf01b6ae2b26c7396f8d2833a13a");
    private static final List<String> CLINKER_OVEN_PAGE_DIGESTS = List.of(
        "3e9a75034fedc1da1979a7b5740f05c6c1201e64d20906a89a52bf24fbe71577",
        "4fea1953fac7f4e0a8fcc1b450c0681db9f0da0492f1a10e8ed49ee809915a91",
        "1aa343e1be360bdf8f63f03c4e86d9a162481af4a5585b8d8db3521c80f6e8a0",
        "0b0963b02a172cc827095a03bf7edc2d61fcc303c5d8e1b24c10e11c16c03071",
        "ddce6592f7a64c752e312367505d79d97d3aaf9341fc42364ef4017b4dd46beb");
    private static final List<String> ASTRAL_FONT_PAGE_DIGESTS = List.of(
        "ac87e7d2658066c9ea3c497e993b35f9d9ae69506babec4b755446ded14607b7",
        "df938bc146f4322690e3c1fb7229bc74cfa296e7972aa830c8a0093a55e29ebd",
        "bf9d2772756a05042a847ec395ae4b7b6539490010cc2ddd8ed9f75c7fd09251",
        "4f9f203482fa9a4a10ac998165ebcf8145d58a5e5317ed7ef733270c8b115f47",
        "739ac9a2571b0d83f8e67c0dfb5a8a122bb8431571b4f27f4a16174974bb13b4",
        "ff6906a9d7d4a5509faa737efbb8a1394c326ef25d18673f10fca58c904405d4",
        "5d14a973bcc287706c2bea68e44da8d5db2688aeb49be8924dda9aca364788a0",
        "fe834ae71a56f58987d8eced816bfc0c88946dee3ba58dfe7bb64431f806f7a6",
        "f9d94158cc3ef7326188c905c92e87bf15c1086f461a929b8849c975ddefb4da",
        "c6ba6c1409eef22f525d6cb11f41a7ccd7aa4b0be7fdb8c74aed9ce080fb3d26",
        "c6400a9ec50492a367b9ee309993367c2f2d2776fbfd7d01aca390b768bed2a6",
        "57877dcd534d41a38739cc6ebaaf7bafb2731f5622dd77b21b1744ba7b104053",
        "456617cbf36b856ade8d4a05e52b3b36409b097e9c66e5395c0f5de7635dacac",
        "7eb6c389a52c09d04a8ee3a0dba2c94bc53644f62731f9ec16143626ef33209a",
        "9f5147a9153ee19d07ca0f1cd40f27b856f6b663047a07038d6b80ca9d658260",
        "debe3b83f6f05ee346c5775777da70cd2eb40fbfc6ef0419fc4480ba29017d53",
        "7a61f3dec073c2a69a57f6cc7610b2e097d151172fab0119ce19c4fe9580d2f5",
        "f889459d975cc4b31ffc7f0c9ddb8e99b49aaadc700d517353bd9c4aa55a8b3f",
        "861f77943aab964c33cf06c1b5d6ed89c5a27018486b7aa7c1fa07260fb5641a",
        "9b350d1a79ee9f8f10cc2ed19c3c40184ba60d6e58eda22f3643ff57bdd67c20",
        "91e084bd3d25042afdee767826f6eabbfd36a6ce20b9ed131c15b0acb1b47489");
    private static final List<String> CINDER_HUSK_PAGE_DIGESTS = List.of(
        "8abb6755fc55e0374bbe9661e187d0f51697489b6e15854678a67bcec1a06e2f",
        "6fde8902d6970102ccd3fe95baca6afad00de475bc8460f355dc14200b408c54",
        "99f51c9020595f02e4a437edd8c6c09bbf34f7f431fe9c14ac27b65c899da9fc");
    private static final List<String> FLUX_ARCHER_PAGE_DIGESTS = List.of(
        "25f86a408d4dabe1c296b2da48479dc80bef893ff2d5f71039cc4f39f00721bf",
        "2fa0f736434220c66ba165f5404b33baa3540fb80baaa9e07c4b2fbe59b66adf",
        "0a5cdab2fb4cd6533a2780f0b15c256e55ea5e1b378dc075bab782d07d555b9b");
    private static final List<String> FLUX_AEGIS_PAGE_DIGESTS = List.of(
        "d55ef8641d78ff03639af47261902d582666893eb30723fe3d4bad2d1329df42",
        "e494314c643fabde32e3a24e9ce09dad2b56ebc84cacda95a5f95778f8e333be",
        "bd1d4d6e40bc29474b5f43fd1582f7f1e9223828823f547a330e12ae3bad546c",
        "1d5fbc26e2dc7c04b8b2cddc57cf667f84a42dcb321f67d95872f89936415e39");
    private static final List<String> FLUX_TOTEM_PAGE_DIGESTS = List.of(
        "c91e3f094398215ff7204ba1874c29f6e7f947caf222c8b58593a07c6f346495",
        "3f4e4c4d19fbb6929a3c1a9acc5ecf631356ec2aff59bb243c7784bfa7130a0e",
        "731f28a4f9407a20b0b4b43a28293d5a860e97752204e278253bcaf1b6776288",
        "b4f781d5fb0aec39aec6d402deb50a3064de4880402102c8bc46bc0cff63f5cb");
    private static final List<String> FLUX_LANCET_PAGE_DIGESTS = List.of(
        "6e2bcfe51572bc169136f86ae1aaa783292403cdf7886e821a29bb688a9ceee3",
        "3494addd3d733c1b72ed8edcd8e623ba070a4289a0dafe786ca588a6d6182b1d",
        "fc63e6119d9635acf646fc743f5a49cff0d8ab05a0e7c31fc3598becf1e3f194",
        "fbcad38f69b06c93a8e1583ad61cc5f2e0fb7e148247f9e788ac7de6b072daff");
    private static final List<String> LATTICE_WELDER_PAGE_DIGESTS = List.of(
        "bda336203fa188eb8931a84899e1c95b45c2d00c829dc0a9f6eb42ee36b9f50c",
        "2bd7279c0acea01772eaf8d7c6154f77a3326803bc557e8665f1852bd97187cf",
        "2899f8e142727d44594a2f2b43686471eed8cb61a5cfb33e09fe30d62e5e35a0",
        "1d34e11ff7ed9fcfc1aa06c6dd3cd36ef65ce6c3dbaf16d8702d7fe4a4295860");
    private static final List<String> LATTICE_ENGRAVER_PAGE_DIGESTS = List.of(
        "9228d7e609b284cccea657058a1706a611386382dc122dfbcc84385fa954c747",
        "aa73aded2ea80b73af3d07e98520e5a571d947d65131d0dfedc42cbff525e7d1",
        "fdd118607872c9e200d5cbb670a53cbd107c37968209c5995f367efe1b90aa8a",
        "18e7a5171335ba440bf0acae15800c3a617b6f8bda73933cc621ef7f4674194d");
    private static final List<String> STASIS_WEAVER_PAGE_DIGESTS = List.of(
        "d024bcd7598a5b92541008e98b553279616e54344ae0c0a2924013b9436a5ffa",
        "fa32b65f371925eb865b5fa1d0d45cebaa6cea5f6a30e31b0722fe075413bf91",
        "526df133013da2dadc06dca25ee1df4aeddeb50fe882af6611bd8d73c4425770",
        "1720ebc022596645ca1fa0d9c3d4e938e75fadef62f28c285fcf4c8d27df98dd",
        "7fcdfbdee1aeff9fd372524c2d338724d5c99163ef3b0a924a8bc8c379c229f8");
    private static final List<String> PHASE_CONFLUX_PAGE_DIGESTS = List.of(
        "77064597bfce3b1fd202fa6f05e9953900d91631f7dd3c81ffdd142f899436cb",
        "f16eaeaedeb4dc70adcef09e94e6eba28a058c04e26e56efb1f3216a149f7420",
        "7f51bbb48b14d83ee08cfb51af0040b908959311e2402cd0cb033bd07b85c31e",
        "d1b594ecf75fce27d49fa4bef0d573da86d2e71b7cc2f904a38b7dce1da3d1c6");
    private static final List<String> PHASE_MODULATOR_PAGE_DIGESTS = List.of(
        "af3bde1d4d4c8124f16e8c7966ceae4cc52e28ee95cd0d127dd6baa183c79026",
        "ca5f2f39700993bca35139198be820deeba62248ce32834bc9b28ea35b15cb8d",
        "10997edabc2a2af319ae0a216e2ffde545530eb217066b5069e028e6a5354546",
        "f2de7717558eeb016aa9b460c5f0fc35790273caccb9b995485b909bdafd58a3");
    private static final List<String> PHASE_STABILIZER_PAGE_DIGESTS = List.of(
        "71e590976c90043f7d5d0e97e2e5d7b16f8e80d8f3eeb8fb04abc7a3f73fb13c",
        "4fb5a93a411ef33967d5860d27e39a6833b211d0d345806e36a6df3c5db0ee1b",
        "e3b0568fec45f5ea86f90a8939f8141bd46aa187fcb8945de040cd27c6774ed2",
        "f2de7717558eeb016aa9b460c5f0fc35790273caccb9b995485b909bdafd58a3");
    private static final List<String> PHASE_PRESERVER_PAGE_DIGESTS = List.of(
        "aedff9123b50030eba1f4c7692d564e7bb63ede3a4bfb1316acba05c98e87ac3",
        "e43de2ff88bbd4f07b3058195a7665ec6993581f631877d13a414f3893827fbb",
        "476e4687ad5a57aaface6a29a8aed6e07f115d5b8a016da724a9d82e6b24c99c",
        "f2de7717558eeb016aa9b460c5f0fc35790273caccb9b995485b909bdafd58a3");
    private static final List<String> CONTAINMENT_FORGE_PAGE_DIGESTS = List.of(
        "f730af868b131ae2bc359c9fe212ee459712db0d98a221a1a13b4c482bc04e95",
        "d4f013a38d90891ff49c808db976d1cd3fb08cdc3ba0d16c3ab2956024e1d7dc",
        "f12551dd9f7f27f772b2c0a8e658848cb1ca2676c6b65b0e8ac07bf15c879e41",
        "bc1bc970dd2ad91af0cd38c4c1ee3c1fb70d17922cfa41469f881b42e91d77da",
        "92cdbcee69e1c660fc0f19ddd67b7797ebfc8e04181d3afc88bb854bc01604db");
    private static final List<String> CONCORDANCE_ALTAR_PAGE_DIGESTS = List.of(
        "6d7946a1e7618037dcb6c381a3fc2edc55cc0bb765f91ce873ac283104ab2d54",
        "eea9e1ffc66f4cf59a2d70193011eaf49cf79aaa9b8e100af04f0f6d79f6f88d",
        "27332da57a23255f4120b61d2a60fd894f352f103e4e76b62c00a122a3234326",
        "18d2db22e9e090903a4e9f51d2086be7ca28e8ec2f21cff70f3245622002404d",
        "21c1ca82ef9c53dbb148bbf109244e2239e1bd5039e510056a83a0ff1192e22e");
    private static final List<String> RUST_CRAWLER_PAGE_DIGESTS = List.of(
        "5cb1e7e7613fc3081f453681ae2e4b51d2bb11605520b0fd579a3cab8c640c8e",
        "eb26fd2d4aa2e0f615ca1697a9bc99f2fd3faa5dfb0c0f097a902b8fa6633898",
        "09376e66b1c1a3083e96d221f3b8929314cbb244ba6bf3d3307f7383a799a850",
        "6ae3630e919017f21f3439b4f141e4ea6210e8a65aa4686bca49e2dd4a62e9e1");
    private static final List<String> ALUMINUM_ORE_PAGE_DIGESTS = List.of(
        "4c6c673f337558b260deeb7addadbc3ed04821bfe60cbdc3753903b17298863e",
        "4ab69e52223439a03cfe22ead9cbdf3496035c45be371cefe272a82e0cdef51d",
        "0ab5f7088927496093bf9e617695165f9ebcadcfa432cde047a2058776312efb");
    private static final List<String> ANTIMONY_ORE_PAGE_DIGESTS = List.of(
        "0dca4f7e0ae393f48855110f08459c9b110cd2356a6398003140e631020489d0",
        "5ba89a4fc9d4fcbbed561e668010aee1aa7c9616d415ce4254e41c290bcc9b59",
        "ac57e49d5fe60b86f37a30b1ba8defe5304d613d8d0f1c31ed74286ea860a096");
    private static final List<String> BISMUTH_ORE_PAGE_DIGESTS = List.of(
        "7f83b88544f22ac51e2df07c46b06d0423ee9b007f3b19369da77c023de2eccc",
        "824c73d3cb2105655f605d99f8a93f89893e63abcfd6ff6c91f9c2b1198c1e1a",
        "fe2116bbbf8619a32d46db7ff3b94579e2b2fc93fc2664aa48c0c8f68c2b78c4");
    private static final List<String> CHROMIUM_ORE_PAGE_DIGESTS = List.of(
        "13d4d43d659b506109b0ffa212703756e7c8d6e915e47a9b6d03cb3cc50918f7",
        "a3d3f6f563d288a9ee8144a99ee8385d88396e76f925dcdea4bcd405b180b370",
        "c2264cd473781cf6fcb239f814350a20ab518a8467d8174708e427516b0eee51");
    private static final List<String> COBALT_ORE_PAGE_DIGESTS = List.of(
        "29a60aeb8dfcbe48e454349e3dfd80772ab09bddff08e9b3c9e6dfa2ca57616f",
        "1353c03c0e746b7d35343d74eab53ecf767755a6ace80d6fe660db4555bbd443",
        "2414655b9899ca98d0947b1b1f80ba0f6732fec67fc8542e5e81d1c0475a08d2");
    private static final List<String> GALLIUM_ORE_PAGE_DIGESTS = List.of(
        "2a1a8eb1ac0eb5a2ed71223c216ec6dbb2a47cefab4b9d78f6a1ff6a27c2e9ca",
        "40fe25904c20b4d90edca186c84a7edd1fb66f88b21b89485c8de79743f19ae8",
        "db6050d1ac4e64ecf7208eba0eda4f63de07e22ce2402bbdd5fb40f9fcc57a0b");
    private static final List<String> GERMANIUM_ORE_PAGE_DIGESTS = List.of(
        "14810cca2f9f70b221c69b2a17bde188fff5685c16f13300cb4f8565f40855a8",
        "4a5ac285cc92b548b88e0891620d8bbdcd0fbf8e34dc820b063be3567abf25f6",
        "07a0a9c5e8a669eb623d5c8442837d0d989a294d6a2943c0ba3b0ede012c0f58");
    private static final List<String> HAFNIUM_ORE_PAGE_DIGESTS = List.of(
        "792c25b6fa712126d53621ed2efb77a9f19b8822527c1c45f62aeb39baec82c0",
        "6450ecfa392b9ad5d00cde53a04f9bc6b309118245b460d6a420293d3684d47f",
        "60dca5ec1e4e603df76f0c14877588962facb9609ff953d0f08e3ae0cc9766cc");
    private static final List<String> INDIUM_ORE_PAGE_DIGESTS = List.of(
        "d3ce38e55f9025ee74ee1adf3aeee28fd814b312cf98f1719d913b69583cdce4",
        "a34a1dc654c519375398bdac11d4317bf077f77ad4a49e883b16a98ae812a626",
        "8a17b21a0f6d0b7d2be989ad7efbb2b7e9af1cf67e264b76ed4bc7dd911e2835");
    private static final List<String> IRIDIUM_ORE_PAGE_DIGESTS = List.of(
        "9dd12d8d869c159e0bda475ba1d977980776aa2e6145d0c51781e6e314b54881",
        "4e68202567e3d9bebc6ae050c9982397c4433d2cf368a44a1445ac364c50e194",
        "3f5fe36a018fddb7b2e5d751bab707204dcf440874f6429686857e76a8697d0e");
    private static final List<String> LEAD_ORE_PAGE_DIGESTS = List.of(
        "5bc84bbdd371f43d122064ab50a14812fbba8c865e0f62dd36993a4e6ef11fb5",
        "11fbd13a95b820a19f23a84d10e4f2ded349ad4ac615f124f1ff5315d286ce34",
        "1a362e1827737bfa4df645d88cd16035f36eb5a23597a62006b4f311009b1cb2");
    private static final List<String> MANGANESE_ORE_PAGE_DIGESTS = List.of(
        "d58a712c19fc3ca76e6829c736a862300b64f6076de4ffc3a70aad508062093d",
        "6cd2dc58eaeb1976e887c1f09fb4980a49437120f08cc2c29111f7d568ba8f3d",
        "b59fb1869843c956e5c77827fe38c4d6eda3003bce4a29c3afd227d1b401e0d1");
    private static final List<String> MOLYBDENUM_ORE_PAGE_DIGESTS = List.of(
        "cb11618e323c70f2d251f90f313fdebb8ba6d8f9ede604eb840537197933ab2a",
        "b9e6f203824ef2ca1b1163f54b336749612f0992331ea9205ce88e3e01c478c8",
        "9e0930202d65261a55a9f6de106b876d80baee9e72377e0ee6cdf60778376a66");
    private static final List<String> NICKEL_ORE_PAGE_DIGESTS = List.of(
        "76404ddcb754bfd9da6d75471437b8f3b2c3f6d1b8aaad088dc9dff9ac234828",
        "2ecb92c0023bebcb710116ed23134ee65ead4b66a92ae4104eed7e2c29a30307",
        "1ba8207d97025a4ce588da1b7626626759c1ad8962ccaf89a068c8d6288fa23f");
    private static final List<String> NIOBIUM_ORE_PAGE_DIGESTS = List.of(
        "09344374b1fa002bf73b08d4c0269bb370f766c134d7f22681e0f0de7b019650",
        "851a2e2269651515551a5f2948d8773f31f1353e8aded3d28eb9513c95f5531c",
        "3fd03842d7e344a3b2d7a39e7de4bd0da03eca777c69932b6cd6264ced8a3305");
    private static final List<String> OSMIUM_ORE_PAGE_DIGESTS = List.of(
        "8c03a4dcb5723aacdeb7c9961d24738280aaceb08e15d96cb78d879108d7925f",
        "5bcb4524f9770897bc5857c492a8ebd31368bd5d53feaa8736eb932a58ad9bbd",
        "f25b97c55b318ae90dded2d621bb14ea84f84e858df7af1a7696c1358e126e53");
    private static final List<String> PALLADIUM_ORE_PAGE_DIGESTS = List.of(
        "8b09f997a6212939c9b562a770b491cc145da95fef549762b6893a55ce74fdea",
        "0637b978bf430c16d53f475882dd777108acc68727fbca65030e011b2daac929",
        "efc7764fe7c513fe84a30459899376a6835e1d3650273ef5c0d2e7bf1c37c217");
    private static final List<String> PLATINUM_ORE_PAGE_DIGESTS = List.of(
        "4a85f0e146e3147929ccb8b83a5db5b78af681c92707549ddfa04584111b6d91",
        "a730654ede7c99536b46c5cae45f9c1863adeed5bef85098ff5d752d2b910cb9",
        "dbbd3c5f39a1e34d032d6aea2c7f6a79d90abe1c5060d050ba94c47708407c77");
    private static final List<String> RHENIUM_ORE_PAGE_DIGESTS = List.of(
        "d32dbd5f9f5a18e77f5ad6f4d2c44078bf405bc6d4993b98d896f081f79cd56c",
        "8f725910451e73b504dbb12a3e5f74af1db0c27cd441c3b02a1d421dbb2f96d5",
        "5318f8d849726f6e576720090b115416c9715100c2aa706fecfb1dc27120de1c");
    private static final List<String> RHODIUM_ORE_PAGE_DIGESTS = List.of(
        "2da018f3644a48b5e4032fb90780ba1a2b3f7d77d1dd4a3fcc23a16ebb32b711",
        "18f7ddddfaef70b1b08d8ac0da68115be019e87fc8d95c8ce16bea1427141dc6",
        "e81e5a1a8aa73b730f35c72cd69d28933d15f74febc066373f6fcb9ebebca1e6");
    private static final List<String> RUTHENIUM_ORE_PAGE_DIGESTS = List.of(
        "4a593110b3a5f03e392cf909cce6daad05e09db673ce5b24479a83fad1978275",
        "122992b9c2aebc2cd509df5d7999e5614388ecc3a41c37a97e0b75ddbc314c7c",
        "327e6fbf6bb891ee4a8abc73effa8627491a945f6f7825c41eee17d76cf8c9b9");
    private static final List<String> SILVER_ORE_PAGE_DIGESTS = List.of(
        "de8e3c76fa55491bbec2ea1ea375b7cd361352c8fc3f5de9aefb6cd506571bbe",
        "cafef778e3f3623b26bf36de75fad8a8e6ac3ab531a18c8e2a3243dcaec5fff0",
        "625f152d3a24ca5c8ba7215236aacf48a2df12c0262401078e744ab51c1d513e");
    private static final List<String> TANTALUM_ORE_PAGE_DIGESTS = List.of(
        "f617704fa4b9e4f8901d7dbe500c0ccff381c7fa51981936756a1696c495fda2",
        "8851c71e264012a51395193cb93c6531e9dca249ba6c49f227ece372dff55a00",
        "341695f49a9ecf31504d41652ae8f6c3a2468b38163e50f13f422d2d1b78005f");
    private static final List<String> THALLIUM_ORE_PAGE_DIGESTS = List.of(
        "f3db3ca96d3f0a41fdac6b8ab6cc6c31f47bb11447e5d4a896d24a73681064fc",
        "0024048c211cc18572d0bb9382fa52b9aeb26d93fc29730d75a09a186b0337d3",
        "750805d9ad252573f7cd260a9ca6d4e091d41d38981e4541de0b67cd0ae2daa6");
    private static final List<String> TIN_ORE_PAGE_DIGESTS = List.of(
        "5dc9487c54ba9d3010d672eb2bd41af5db79ecbb8fe478028525194e01529703",
        "1697e93abf1d9419d8b69f2beb06b4d95dcd6d359e9facfa6e7228c0c323e6e0",
        "8612e116e90931c14ace5b786c03712a26e011c5faf960dac6c3a9aced234656");
    private static final List<String> TITANIUM_ORE_PAGE_DIGESTS = List.of(
        "4c4ec8c1fe65da5d4871c14cdd316518e569f34449b14d32f8d461135e567cac",
        "eca0c65170333a2c60886db4df957aa3bfa8ee14d11eccb6596cc4f756b009dc",
        "f43310c56be0cc2e77722f978604d0e57a21ed0dcaad9ff4c06f3df45ec66203");
    private static final List<String> TUNGSTEN_ORE_PAGE_DIGESTS = List.of(
        "04474bf5dcbae7d3b4c5f8ebcd6b18b4d02eebb14649dab140aab256b34f97cb",
        "578c667ae88e4607cc3c799c7c9c2b3de59d3840426dcefa41335f90793a15d0",
        "3760b9a70d2932873fa56f68c0939ed087953eb447607738b81d680fc95b5c76");
    private static final List<String> VANADIUM_ORE_PAGE_DIGESTS = List.of(
        "d2a08aa860cb8eb8df9b4630c74209481429c255994b35f42d1016b8f9a8bc12",
        "7d6c81b87699212f93407343cef3b88997283b7c908e6e0ba2fbed03b5899d15",
        "ddcb65fca42005dd040eabf1744874f41a1432db653fe6a8e47adaf5c47ea496");
    private static final List<String> ZINC_ORE_PAGE_DIGESTS = List.of(
        "8dc24c75560405b9cbb6fc4e113da989e5d9cf8e6d6095c60d4b7ad0bc79d7fa",
        "cf544c81be1ebb090b1fb36e0bb03051ee6c2131918bd0cad5a74b83fcaef178",
        "e524fff922f586db1b63eb3577836b1a08c9879b8ed149268552cd4d4c064d9d");
    private static final List<String> ZIRCONIUM_ORE_PAGE_DIGESTS = List.of(
        "7c5d5681bc9c1865f62b49c5695e800a1f8f6ab0bcf0e3981372868b9b078974",
        "c6ebff1d68ca6655ef3c63aa89a43a94b4db99e2a9d4d33a3ca270c526f4e90b",
        "3fb3387cc56a17ad10fbecd173b0512a3283ec1c674d18a6cdb3b2c84da1bbcc");
    private static final List<String> LATTICE_COMPOSER_PAGE_DIGESTS = List.of(
        "4db632dd14bf6ba6f2396e2c54aacde48a0d7e19f6df4b31791794d1ac986ca4",
        "5414076f653b7de34dc3ba0c20386ea230cdf1903ac2161213dc89646ee5196c",
        "cfa4500b4f4d8fc0b7159abfea2eff4c39f7ae59484742e0971feb405c1da2c4",
        "ce061e404609677bdccae33ded8fa02ffc120b59c682b5c703ccb123f2c17b08",
        "770a03f1c7a2c9b03ec03b3aac387e4b85ed0758e86e48beb7fe3ee28b28039b",
        "fd7f94655c206664aff2c8a05507fe41cee0bf94f679a5c332d8bb2e6d7b5929",
        "6f6068feda5055c55cca1e667ae5305014723bffb8b2cfd8b8f8fa898ce230b9",
        "66ff1ffa8b436f0b7da9a3d079c779cac0381b5c6d0766f3799b63c35cea8c3f",
        "c7bed358fef62e2aebe2b4e8fe6665bb10e20ca22a14e04e3bcca444a36baf34",
        "46c4229068c6105d3bf36a0c2ce20b1a186bf22bc030421e9363a063badeeb6f");
    private static final List<String> LATTICE_INFUSER_PAGE_DIGESTS = List.of(
        "89cdf834ee1730644db63c68d53ac738da041ce780066d418a34d3fb10800a74",
        "0f6deaf425c0e02c882ebb9657c2b5391738114ba1a4c8061e0ec767a870b39f",
        "a605c4de866c33e1c6f2747a859446d451a978dedb74e401abedc9f0e4715c9b",
        "e5946a93da8cc74890c5c2dc1a3d1e9cb5cbd9ed39a7b2c7d266d4a1654adaad");
    private static final List<String> LATTICE_LAMINATOR_PAGE_DIGESTS = List.of(
        "e7065630e34e096f3a013824baf7514f148c5429a77b1d3babef00de698b7d00",
        "b64afc2b2eb84b198bf6f5ce720eaa34c660df4f80b9d5d84345c45f5ca507b8",
        "fc0ea88894f20ef09207dfd255f490fc30ad914cf8556d743afaf5e1ce2d6205",
        "de67fd2619907fcf82382922887a3f32b40e6c0f732171a472f96d221179f29e");
    private static final List<String> LATTICE_RECLAIMER_PAGE_DIGESTS = List.of(
        "fb5b5cdb2848a96ced59ff7a2a39081e81c25dcc6666fb9ba0a3d83f7c791d75",
        "9212066cb8cbfd6422b341f62c102ef551fe2bed9858db5f5e16841ff380a6d3",
        "198dbb081db8f87128ee593cd246afa507d0aca7045184e94c786dd399827f5a",
        "37e6f3a4a282c273f4ff86f1190891ffa58b5f5bd41beff74ee55e24a6ac0c41");
    private static final List<String> LATTICE_SCRIBER_PAGE_DIGESTS = List.of(
        "1adba96830bb7abedb53150a059249d5f543ad23f5a1ef7cc27bdf26db1672db",
        "697bed7221db695799973231c6a088e71aa2fe43076a365de2896a7b12dc13b6",
        "b825ed9bb7e20675ae54db51ca4437286b015bef01946d7d3699e3f11ca76362",
        "e3c4fffa3dda26ebc473680d7dd9a1773ba043c0a977ebfc404237ec297a7dd9",
        "36ece4eb93c5cbdb9a6205cc58e0393ceeee1940af4fa55cb57d77cfa818908f",
        "2bd7279c0acea01772eaf8d7c6154f77a3326803bc557e8665f1852bd97187cf",
        "43f57209ebb511698f1f4977506ea1cdb2f9c7b2289b88e8994ff77565c86294",
        "80b8f8c8f0a14f16becd7b9ad7baeda9123b81ba798991201a14924ef8702c0a");
    private static final List<String> PHASE_COLLAPSER_PAGE_DIGESTS = List.of(
        "beedd29d1c8c536fe921c9ce57bc8c15bb6dea616c12019d0515db8fd7e98bb3",
        "05d414ea11d0f2f4ed9f30dfbbcdcc2213f5fda31031bf94641b4a934369b758",
        "72bd30313aba6d0a1f039f91016bb5a957c724edf57a1cb36e1fa6018524f02a",
        "9f4e153b44c39afc6214e776606a8f1bc97c5d6a3cdcb684463cf3280b877ca8");
    private static final List<String> PHASE_DIFFUSER_PAGE_DIGESTS = List.of(
        "06c35cc3ab9d247de7ad0e2d5634db4efebc7e09fa480a59931ef4e969854b79",
        "74358f8773aadc99977b166c137cdfcda7ac53d58eadfc59df3fab47c1bec37c",
        "3245a2275032525cc60924f28cab551b6e1e89678f9b535e63e9c5e7e3298131",
        "8480f4e6d3bdfa2f3555f8f7d87ac22cb954da7d931a375ad1c6fbc1db59432d");
    private static final List<String> PHASE_EFFUSER_PAGE_DIGESTS = List.of(
        "ad99d6946d5d7e7af2e255dd3cefc5cd206a7a2f27cde54cd42e3f97f7777424",
        "7f82f425d7adf6026a16d13c62e3fa481e7ba652b2814f0df5d2aac678b9ae01",
        "18df39e2afe23830f317f45163c2e382c63cf0d30538ceee1c29e44528bc6c13",
        "f2de7717558eeb016aa9b460c5f0fc35790273caccb9b995485b909bdafd58a3");
    private static final List<String> PHASE_FERMENTER_PAGE_DIGESTS = List.of(
        "b302eb25537c779dacebb4d81da5de58fa0be081a767b003875474eab7561c9b",
        "240d0cfe96670e0029c43961af03b3b56a48eafc532119ada30eea650000f6f9",
        "20da9757decfa43dfe4d866afc87fb8e8c1655d914e6c057f316ab82a4ba3a61",
        "fdc1358f215b2862bd255d6c18532a35e8cc982fe6a0274e5a0154be40497053",
        "26adcd780a387ee07f269f41333e41c007f1970ffed491bebd5bfdc0d4118fd5");
    private static final List<String> PHASE_INVERTER_PAGE_DIGESTS = List.of(
        "69561616628476e62a278d44411064e17c427f058ea96a42db5425c5297994b0",
        "c4fc231e209498b24bc2069e72fa830f7db0b9c1d6cc94b2d00632976d150142",
        "d6217a8fc69984685cd0a3de352cdd14dc9326dea15b7168a24ea92a539fbbe0",
        "183168bfaf03863786a65f7ab4cf1cbace1c0e71870e2ad371d718a0e6b6250a");
    private static final List<String> PHASE_REVIVIFIER_PAGE_DIGESTS = List.of(
        "f2e49dd3e2bfab23a32a8471ee31be61b12df025172ae035cdfed920327145c9",
        "75f1e6209e5017712a9cf1042e2d181bf8eea8246cb45f3172ae0015fb0cb2a9",
        "6196848389ddc44e464642fb499d12701dda2f7daf6904811bb62ee31b4dd496",
        "83240f6da90170b4644c870d1f363dff001364516da7fe13e69e17bce52011ce",
        "9d3aa3e774853012852f539474a0f1a25914321257316f471b3ac1c4f39bbcce");
    private static final List<String> PHASE_ANNEALER_PAGE_DIGESTS = List.of(
        "fe60b6cc1ce8219e1dc0c0673f299d5490a19ce8d106d14f2779745f8e9aaf45",
        "57133179e1b9f926b45bfdad52f875ce665768a5c2f5152510f1d6aad930eae8",
        "859d0db0702926bc56b013c783793d4d57aa2f8af1daa4094b62912710b325e6",
        "1c251d10d91dd41f71267fb15ce2f7ac0545fa24df6ab0885c2f45ee933c85e3",
        "846d63f6bf2edf5934fafd5c943335c3bca8ee974ac7aefc9b35fda1ea56eb2f",
        "0661ca3852eacf6fdc552106a3d7f3bce4e3720fede4b18e54a9901eeca8a34d",
        "80825b10db3534775f5240cc7e50deb98bc37faee8bc5d5448fa8b6f525e9ce2");
    private static final List<String> PHASE_RECLAIMER_PAGE_DIGESTS = List.of(
        "9b62230bdf7543af95d7394ec6481dedf29f025d58845693ba8bf860e879d97e",
        "d518dc9ac6b153a88d870630730103e07681dbe1caf597ff38b9786ea1a4afdf",
        "c22ea9e252614d990ec5deeb234de3c743ada2c0e73eeed4a6bbba654c529037",
        "b7829b32dbfde2d30f8118a94fc4122d2141cb3e27601a43ed85a5fbf335db13",
        "d404ed27d64860b1419dd80892889237d420fab1775a85adca7ea0f7bdb7561b");
    private static final List<String> PHASE_REFRACTOR_PAGE_DIGESTS = List.of(
        "22e71bc9962fe62a319e8486adb648c48a38eb6fb2d76917ee97ddabd7a3b2f8",
        "0d8034d63d8a70c74cfc398834bf756372aba47dfe4dd1701cf4ef2cb7d57625",
        "0c4b36f1cc752e49ab285f063027e13b136403bab9981c04cc2d8866ff66902d",
        "909ac59418d0fb33714acaf5859fdcd5bcca52801def67c8b622545d536cd918",
        "695de16934da853527705d90d02b774370ef5fa703ea5b179586048f5238b8b3");
    private static final List<String> PHASE_SCEPTER_PAGE_DIGESTS = List.of(
        "8a4ed64546c92fbb45d60fcc5f6925c155f56181013b3fe30f7da2711877c75d",
        "dbbb08f30878023f8714f094376b83f5a09829cb1609e02f43f0b927d4ee7262",
        "5ac7a2ea3bb2590a3b95182deb2075986978d4fb2b518a8831c00058d3db86b7",
        "3ad5fc935badeaf908effd27bcb0f1df4f4e0050f1a0993eda6cf2a9ff24106b");
    private static final List<String> PHASE_SUBLIMATOR_PAGE_DIGESTS = List.of(
        "568add39a9017d7fbf90d40461e743a695c6cfd45b21456f3a29020589e7902c",
        "084768d42f84829c07d32600a0480d07da73e3a2d3c97cdc1debf5f41b13ad41",
        "e5cba93e2dbcf469ae4e514beae140a67cc36d73444b0af482971a92f4b4295f",
        "e5277df06dd8ffd65b1ed691d64ef7cb95e07192bd07b7b7864f90b036965884");
    private static final List<String> DIFFRACTION_GRATE_PAGE_DIGESTS = List.of(
        "fd5c4326f94c1d08e070f3756818594c9d804f7eb3fec18c1a2658ace3b7eacf",
        "34ee249d25ec3c7a718fa1bd0279ac2ac4894523c690a8e04401af3b90a31fa1",
        "b55b68771cf2ca2d51eee8a21ac35576d6b4a35f569f5b99082a3ca7344d1092",
        "67dd5e3baf180e10904bc74b8700779e60a566aeed361bcd75a56deb82d2c690",
        "98dd4d8d88bc75cf2d76976ea881b21b765051b9226222f0cdaddd206246cb32");
    private static final List<String> HYDRAULIC_RIVETER_PAGE_DIGESTS = List.of(
        "4e720b19f614704f412ba154ec5a4813e7cc6c051090e511552304d6aa3d3196",
        "4fc7f63e5d2778f2d3dc6a90f205c0ffd4c9f2d1d42e3a1393bc3a9aaa9e398d",
        "4f221b0b3d7d95c2b28f5ed37bb5e24d3c16cea2261ccaaea69bb70ca4963f0c",
        "e734c1f27ad7f8d61cd3fe0b38de7de51d4d4d2b1f8564813d2d7781bad4b2d5",
        "44fa4aed69c9511336ae2728adb6b9859f17bb54435e473d94fbb0a68ae26498",
        "d4038206c16662584bf64a697e070951c6fd58e45958e221d47c93ba70bd9f07");
    private static final List<String> IRIDESCENT_BASIN_PAGE_DIGESTS = List.of(
        "b6706ae1479370261198cbe228e7811f1a1814d5e9517d10b538b0af06e4c35a",
        "d77f1f4f003d1dcce5b08825737d2083fe5c59168465c538e011bc677e684d3f",
        "a7ccdb21d3f0b85f5ec217da3f90e1cf2ea5d45caea2289dcb9d21d89ddf75c8",
        "d50bcca0559cc07adaf71b632bd93aab03e4a931d213db7d569625731a7beac9");
    private static final List<String> LENS_COLLIMATOR_PAGE_DIGESTS = List.of(
        "02e4cdb9aab38b708a314dbd30dd589de3263c92a909fe810f3d900e4ce9537b",
        "4cbd4740c61fa7947cc77e2e9a0cb86b63f3817a0d2a1c76e794346668e7fd9f",
        "7494ed8a0bd1ef018f9c6f95eaa3e63026b18f9c800beffe3daf056fcf2a0817",
        "0c627480a5127effd04a0f9156301bdbb03df247849ddc7854b2573b6a7b7f78");
    private static final List<String> SILVERING_BATH_PAGE_DIGESTS = List.of(
        "8efe255e122ffbab25130e4a9732a1e0a97117d98aa7ae679cfcee944c2abb07",
        "fc78d6d4740edc621be63961bface311a75edc4a205656ab1b0cf9ccf6228a99",
        "e8fcafccb1919a9e29acbcf9729a5cb2dcde34c8995d1a30547d7821e9caa9ff",
        "d1b5786906b71de168757e553c2918e5a212be56592d39e37abfc8cc5f1a04b6",
        "360a8d67eebd062d65f3a53f2feeb8e01d18db9c97a022c260bbc82a9135f220");
    private static final List<String> ROTARY_SCREEN_PAGE_DIGESTS = List.of(
        "05c8304282c059b7af54a108fe6d363f9880f622d5f84605d6b543c3c91498fd",
        "d89666b896ec801ae60f70251976f0782d592092282563a5999c7abed783c524",
        "d91fe13bfbdf13e800847977bfe9d04279acb379e01b1515ed7f117054eb1d10",
        "d797d53862b1061ce08a66da4f32f258e57f653292c718cb2fdd709429ff1bc8",
        "493f6e84f8b55c0b96fe234578478a36986215f309fe0117a5affc611e7d68d8",
        "9d1ffce0fdc2bf3050316fc55f045e0f22c08cc1d557782884b83185831a2d39");
    private static final List<String> SOLAR_PHOTOLYZER_PAGE_DIGESTS = List.of(
        "a0287000beda6b31dc34c90b5be100b41b5ef70c0e89ad1e181d88b017311111",
        "ff20a51f498d6f8700477f1ab882b55ff3036bc50a2073641390cab95d0c810e",
        "a7935b3e095b0fd8309c53753850b15b9c7bd9a5a9e400e214326c56841b0c3b",
        "7d3fa6c5ff58058d3b5f7e993c80221bb5333da61dc28ff1e88551f4f6de4c34");
    private static final List<String> SPECTRAL_FILTER_PAGE_DIGESTS = List.of(
        "99719530f1f1f20119430b9323f70f9f022fc78c76a944a2abd15c8e0679dff4",
        "44a61c746aa92121ba3196bd753da55c159ccaf8312684999387dc897c5c2a5a",
        "2d4a7a2f0b37a087992db2fb180593a750f3cc7e63e4a0b62ec0d8e705a757ca",
        "98dd4d8d88bc75cf2d76976ea881b21b765051b9226222f0cdaddd206246cb32");
    private static final List<String> SPECTRAL_LOOM_PAGE_DIGESTS = List.of(
        "56852ca9c92e75bc3a28bab25ac8dab79ea16c3046a499754a14e4c1f21af42d",
        "4120d97210fb1407d2b1a7d4fb96e9dc70e9a4aa8814ac218039f59ffaa80e8a",
        "1707a4b9ce712ea329aa6b5a2cef40f41d1d3bb15c6095804f98001add89a0cf",
        "9974226fac848b32c64e01d5e15072748ee4f3a784231760061a414d91bfd417");
    private static final List<String> TWIN_CHANNEL_WASHER_PAGE_DIGESTS = List.of(
        "589a4a223d9469fc1e6f2556e7d5c76af06ad0f4192cf6a1f7407115b20459a1",
        "7899da8cc6aa4ebdc63e887d2d36619b63148c7affc357d0fd2979eb30f4cd75",
        "6d2566db334bb84b486f1d67a3a117d11b513d45f1df31dda5bb08ed94bea0b6",
        "5c80774894ccfb99da18bc0ddf0c73f55bf0119af48f5f2e7bd723b06b2cb0be",
        "7602af79f2de7afd86890a7f71d0514a8f9a2ae60d1055f62114ba92ff39c28d",
        "783ab21d0f17439d4caf9e2f1e74197a68f580984fe247df429eb30cb359cbf4",
        "572eb01ee1e598509f4ea1e64db2726c9fb9ff80d8ea23868873ca037812b67a");
    private static final List<String> HERALDIC_FRAME_PAGE_DIGESTS = List.of(
        "d4c7983d2d246b170c642fc713d0638ad9fa2fe81c7cb68789ad82b9ca2eb4dc",
        "ac908df2ef73e8cd79b44115d00e3027d10276e8d06c79864f30954dd55d6fa9",
        "601d5351d95c14d7ebed1760520955d43e2fb40e2adcbbdba5c06959c2972a86",
        "0cea109035ae1a14d699f9f612d40b71c59c0d1da323e03b2825bf183fcfa53c");
    private static final List<String> MATERIAL_TRAITS_PAGE_DIGESTS = List.of(
        "861501a2f3268601f070eac92fe69e4293b0b3fcc7d933bb9a7022081e1a6e04",
        "a83788f322323442285f630625a3985734646759314ac880b9bd3258580431bb",
        "d129c839f10bd318bbe150c148b7448270bb8ebf5e84638401e5763776ee6aca",
        "22d183cfb0993490fc0e6fc9344b3df7d521cc95d432158b8ab89ee7c543765a",
        "3fdb484b2448b91104c557ffb625eafb1ece24d305ab9f508b5be078b59240ad",
        "a47545d6ae637dabcf09fc992e9950cf913520263f80cd1f3aad110b0e4ef266",
        "7e2af72c41c92b81e0c940c937467ee7736c38c770989865917a4b632ee776bc");
    private static final List<String> POWER_CABLES_PAGE_DIGESTS = List.of(
        "ac6180063fb27f3e78445f7d1215c1f1067715de361579b6cc9b192b3a4edd1f",
        "b2c8533115cada834c1a5c35671332449f0c4f32ae08f66a5f3793ab63ce918d",
        "fe7307723a9040d41739ec8266f2eeca817d2c0d13974aebc438c457919e2eac",
        "e6f5eaf1dda6236020461b2f438fc566f6fec16ae55ba6d55d95c3650e9dae21",
        "f4b05daba5e307bbf4ab4ea9df8e61ac05b88478bfa99ed34cedf708f7f12d32",
        "7fde8686ed9b61214d152acdbe2d4fa30249802d0dbecea27f2aaa036b6df29a",
        "0267ed3848bb02462bb7358a267cefdd93b8678029c6e765a9d3fd978d7e00a9",
        "22f8bb9392f9dd4ff5bad94c5101e4f3d160f50efc7a6e70081a9c35ede645a5",
        "5787ded833164c9d4c41b1cb191a22eded63d843d5ff3377c6cfdb42bb4e9250");
    private static final List<String> STRATA_EXCAVATOR_PAGE_DIGESTS = List.of(
        "93ab9466bf630d99dcf7901be93309fba4bc33105c7da4c3eed3bc79166df159",
        "5046cb09acf97b3fb66f0125c178f1e46662d9e62d6f442c4f910e0f5ad56560",
        "ec1add7d8afd6176841cde53a65b5d4478e65fef55b73ba2201254b31615b99b",
        "b54b9949f2b2af79116e2d681a9a7eee7ee99a12f6298d9c09f3bbdb9b67ae67",
        "c832e1e655540f8af9f2d0b365e8053ea1928c98fe43169359c77c1b7d8a586c");
    private static final List<String> STOCK_HATCH_PAGE_DIGESTS = List.of(
        "7bfa8092453391710adb6e5bf4c378d1b873be0a52e814f2f690d0bee8182fb3",
        "2f091f592ade04df2d160ca67f6ec7bc28bfe5f2e3cff048f970ec39367700b2",
        "ed2a20b45c503fd6e03264fc18ac458da3d90e53f1f85468f9b0bf4ad4e36cbd",
        "41325ce079fdceaab9b9e5d06920636c13b7a21ea6bd525f704dfd22ef968232");
    private static final List<String> STRUCTURE_PROJECTOR_PAGE_DIGESTS = List.of(
        "a68f3bf43528b161ff5a3a40f3b9b45fbeb099d9edabf499b9f736fab2038b28",
        "a105f4857fa7dd0c54a04f7667c821f5d10ae4ca66c43e54a113498690454f84",
        "4a7dd89fe7a915a68c09474572f4dcd313aa7514ed741b7729fd6f547ca2df16",
        "d631883d2a7a8afe3faa0fceafdd4623e5780006663f0bbc1fbf2e7d6009327d");
    private static final List<String> ORE_MITE_PAGE_DIGESTS = List.of(
        "ef9385d97ab0b64e97ae26937d7bc253a4318f65d73489127b9c7ad58abaf9f6",
        "a0d846795126eabd301ba55aefc0c001e2e5d507235b7eaf6038c0daa2f9b1f2",
        "5967faab8186b97eb5cd3da9451503aac9754564db76c0290aaac5369707e931",
        "712e83f098aadcfa95ddc299da37802be68b3e22a6b737ccf51a44c3760e593f");
    private static final List<String> PROVISION_HEARTH_PAGE_DIGESTS = List.of(
        "59896d7cedccf30beb40c790fd3ddf68f544ce61c8493bf26d48a56c68d859d2",
        "dfb16f77f9b54ca67ccd8bcb9ac6f5880383dd9a6bac8c1ce5159848165edda5",
        "6e0ec3b901dffabf63226b450f0fed60f8bb9c492fabc9b69aae9f8e9ce108aa",
        "30f73f606db56b0bca25807dbb48ea9f99f43c65d18e835d53106264d28ba424");
    private static final List<String> QUERN_PAGE_DIGESTS = List.of(
        "5e3068e05475c688473b2928f8ee6c9a4a0a6b4b5d6f0e093f3ff66fa7266ccf",
        "bdaf87e301a00d2d01557ae15f4aa21fbce6315c2fcf2aeed72921577931dcdd",
        "00bf649e893c6e57cc347537a6ee931645d2133cf945600f4a47cc258bb253ba",
        "b94cffc3b323e8cd21f05c61dccac66764de0faf30217d259faf5431de8cb7d8",
        "737c53f39447182b7ffb03cb3d77ede0d1772d92bb7e5007ace5f47bbc9d94a4",
        "1a194388c77eaf350336d81980981f0289ffce5c6ca3bb7906f7c499573e2fc6",
        "e43c219231eb11e339cf4e8bfac3e25b4f57338af074d50e4b7f6ca907d5ca74");
    private static final List<String> RELIQUARY_STAND_PAGE_DIGESTS = List.of(
        "b4dbe68c7578855a6b00e94befd4d229a132c330b61f07d4487464ff19c55b11",
        "630b7d242e94ba994274cccdc9c908c838c67cc38adf2be12d7158f6b6a3f134",
        "3f71071eae6b18e89fe584a4fdb3ec9e2f6cab8520ab4509bae33236d534102f",
        "cfdff409fe20f2a3b855c1a69d324b52124d002531b26ff0caf2e7381d58d9b5");
    private static final List<String> SANCTUM_PYLON_PAGE_DIGESTS = List.of(
        "a3b3caf86f14cb5184fe3f57d60aa0c6e85ac87897e873ca72b6df2f58f6a50e",
        "cdcc3afd0ba0c044075f5a0a862f7d541153fc1fcb46a0f9de69f1d88cb290a5",
        "6dc57c7acb052f276219d3b8439941fb33d9974b5b3f0d62227318198299767e",
        "78fedff9d7fa278993fda726c4c2ce536cee5f4a6e011336bc7a06d18f4d1e50",
        "2906d481df150a119c3ec9b8d4fd4237acf3166517e83af7b37746c02996331f",
        "7694deade9753f07708fc32fa35ca68d917216050e4a5a1998fcb50bbc436f9d");
    private static final List<String> STATIC_CREEPER_PAGE_DIGESTS = List.of(
        "fe8a448fbb360455ef2cf5b397dc54abc53c49b03b1e4575686617f62983263d",
        "b460e719a7e00fa1f09df44388576807a123de43914daa286c6bf7ff450502f6",
        "28d4b1b085396bb0094214500ff25a3c0a70392c414b8bf199bf63e1b74bb229",
        "db88ff64bc08a0aa5e9393f185c3cbb2ea8ca49ba92df59c51f86a040e08a808");
    private static final List<String> ASTRAL_WAND_PAGE_DIGESTS = List.of(
        "3ef7338b240dd36c9019fb1cdbf5d179c541604602ede5ce2d7b779ee3477060",
        "a8f41f27a5eff6bc8fc6a5f42b2b1945456a7f57fd9038f4016ac5fd5c3f0fd9",
        "2eb237998d31fc1aa4e95272f7ebfc57e666ecd2caa2febc7618cb41a09e19a6",
        "e1ab8ae13ded9db2bc1bb801738206c0970eac60467e68fe4753706effccc092",
        "a2dd6d2259037425298b09f41c2032ba04e8aa5f82dce704f1092d2233aaaf16",
        "9816a2035f10aab45290cf5add1e2a14db47bfb3ccf64aceaa15d002e36caa66",
        "af44118e6e6e4599c04a1e36603fa5bad2c1d727434763d4868a149662841f5b",
        "900298805d2fe329b2e7e2219c796e8b680f4ad4cf3a04b0a3db78acb1e03fc2",
        "ae77d49c52098a19bb343ca671f6e66b04d1e3117f75affb7e7ecb9b54d88d15",
        "d7bbedc456ce6663db9aee8eaffcb3a43ab0512f3b7fff9d7393a5297cf4f6a2",
        "8f364de8de01bbddb7ea5e4fae4ff2e8d71021d4cf9e4b1e5646112d78879ebe",
        "f9054b4cab0ac69a59cf7facaa6cf3f81ba2d3db9f3c97791eeb3c5a3d320553",
        "1f559e2af4d3ddc217a5ec4230100029c6dd24ce16ad55d6016a59ca0594f075",
        "31e0882037c1c59b6d3f5d247edd93ff94ba441b3362be7b86fe4a5d217c80d8",
        "44670495e056fb2626838cf4ffeb91d3d46390cdf9e88501bedd4ee8295852bb",
        "a75055e504dcd83cabbdb879cd674be762852bf167a38a68d62f2c89e1d2a5f5",
        "2b31c6113d667034fc4c6f25c2d05bb34ab8e65e241b0d165f53b9b465a76711",
        "7c3124918b65056f6c295b3e4cdfae85712196803d71e5658ea72d026d2fe8df",
        "c4ff4078e03b543e8f7b8ae63920b349d218e03cc0e3b94c5a79152d4e9fd91b",
        "9318646a963c05cb80b2b6e6f044d21bda90d594df9577bd879a312da44de0c0",
        "52c5bcc77e5c74723cd6df810a0a069c831fde4e52eec81481f045efd2ed71dc",
        "c3ad1cfa2da2db4f646d5b0be5e5f1598a9a4231549fefc7ac2de7e16ab1d2c1",
        "c5238265a6b15649fa65ac8150823163808b75dcfefa228e794ac7bde30f1f6c",
        "b70e6130c33bd9e349b3d0172b97bbd0c5d9bfae11f1363c849a7ee925b8940d",
        "6e15e4b1d8828f8aa3d3389521f2d311c1bd0b0fc6c0755ea9b5d77ffd18ed97");
    private static final List<String> ASH_BELLOWS_PAGE_DIGESTS = List.of(
        "fe2bf0bc56f415c06818cfb73b7a23a5adbfb36ccff4eaed1f5cd0611bbac193",
        "5bf6c8eb65b8521f17ed516d5b68fed660252635da469dbf90f5be14f56838a9",
        "b7f90536de64f0a67f676cb003c1358c6dc70631fd9570a9263e9d9e0b10b6d7",
        "72ffe5fcf58903ac022b727dd8063a5d973b5396a6826a4403afd5bdac42f510",
        "ac65a3a4202548f2bc72153374187b6941c1a49759d36c3fd1623689e84128bb",
        "0d1d063d8b7c960046a947365255385bb5fe79d7f08719f5aaea83efbdaeb578",
        "15efcdc543c56d5757230f0295b4a6565ea9067f82474879b25a219da31fe56d");
    private static final List<String> FOUNDRY_RING_PAGE_DIGESTS = List.of(
        "5f89503706aeff76d0402a449575900c94ad7409d7b86b1235323e97b47114c5",
        "6eac009b123aeeec55cd147a17071dc9b04e3321f672871683ab054212c2f960",
        "2966c4dd7c961b7d89b2baee296594a65a98ab9c42875a9e56b3eab725428072");
    private static final List<String> EMBER_DRYER_PAGE_DIGESTS = List.of(
        "15262d5ae780b12a9715de2d24c9905569cb4b7257a61bff9b229c951b253c94",
        "3563b4b60f02948882f5ba4e8a913d24713501557839093a8026b0840c4637f8",
        "fa422f715506bf00f3d13c6d8f7968a3e21427b5ae5da0c787a80909df1beab6");
    private static final List<String> RESONANCE_BASIN_PAGE_DIGESTS = List.of(
        "21c5516c17578f8a0233bfe70aafb1c610e97c0fffb0fb38320190dc7aa08adb",
        "826fb15f90294b70c06f14e5fca21054fea07f3cc2a0988750e90a2513c67c5a",
        "932c588c3f85843d8d5fefbed209dd0cac63535f4ce5d1f349ba7f060213a150");
    private static final List<String> SMOKE_RACK_PAGE_DIGESTS = List.of(
        "890eacb504addf6a5a062937213a927de7e037b54d1a9c23d8642af6924c9ba9",
        "6fc639b994d30b2435a5412481ba35ac10a4ac5d8f757537ab690b14cdc12621",
        "4f73bc18c7c48377b508fd5d9bef74b8a6fb8f51f5d4bb7acec20019d5eaace1",
        "a4451d50d9a4c068c477c0ce8776d57d5546117c5230bb63dbf8ac1e2f88320f",
        "c2de910301c162c6ed2e7bc57f9636636949e93b953b2ef7b86c184d158b2278",
        "6e482651d732e1e95aa80bcc147526d34b5d84e2b317d42dc308163d7735b4c9");
    private static final List<String> BLAST_COLUMN_PAGE_DIGESTS = List.of(
        "bab370d6df4ba22ef5ba3a53cf1f6085b3ab992af545c226f2ef9a5d3b47ce3d",
        "57f73792338689f3a1a0c345315fc76898d61e9f4bad4292520b9af172c1bbab",
        "ea7473d19f6cf68d8681d10a00fad775132b2d8475c6ae82208851663f50d451",
        "71da7d4ad2b6dc99170b2f1d6b2d72b7d92f83fc25290773765ccfb19b042296",
        "6bc12dd64548039b723dd7bd44d0ba4b7bc546a49a77bc50078f766c5333d5ac");
    private static final List<String> RESONANCE_INFUSER_PAGE_DIGESTS = List.of(
        "3618ff8ffe7e3802e21e4cd37a0d19631884fa57f7010de426c19f659b0a30d0",
        "b4480f38dd147641549ceaa97d6606cd2a0f9734652650e26da8f2691b24d618",
        "01208f2e17df49e815d992bf02788a2a3c655f982078b38b2caf666da8d24184",
        "653cafc77247e7b88c1203f28b08114dddd90a55dc7905bd9c0def308b89d35b",
        "886447c16052dad8736049fe3f5b7d65c1b374331ca8debcbdd1f4597ef2a824",
        "92ced3a302769a733e6ef579a442892d63b4db1ef98c946f5c2342fd1a15a0ca");
    private static final List<String> COMPACTION_SPIRE_PAGE_DIGESTS = List.of(
        "b93868824c90d1819b4ccea1209ac17a41769d56189bf664372a98e0e0c8532a",
        "70aa8112c4e93fac8e2bf8790861c93bbb8e85c3d827f2d447d71ec49559ff6b",
        "e66fee8b9d922533dbf8571084de7c5ac92663c1360b8602a8ebfe80152f9512",
        "3d51afa702701b1cf6fadcd33e1340d03a51052499b9d62c377f6d7b8a8afd1e",
        "fed980e1667eb0552182ce6b47a1adb80feff1ca23cb91ae99bcfebf190137e0",
        "3690e448a0fe9ff0fde882532ba4ca5b1b25fa81771f0a4c019eb067857c2de0");
    private static final List<String> COMPRESSION_PACKING_PAGE_DIGESTS = List.of(
        "fdb0aa5e4e6839a45154c5a5826bec43c26b2052d579a7bbc1bd7ce5c064633b",
        "148024c2421e0fa8f46111666ae8bf1355023bacd07e035db87338cd841e9923");
    private static final List<String> BASALT_TEMPERER_PAGE_DIGESTS = List.of(
        "2d8c064623b7b503ece3b348a0dd6907a2a7f5e487074524ea5d6c813ccb315b",
        "2208d547316b7b7e74166edab1db599cde322148aa63022489cdc99575364b8d",
        "0e941bc7350b312a61a9ee03827acbf8f4ebfe969d8a7aa5532972855c107148",
        "6465758762fdef7547fe65af81aae22771300d62b3fa78abdc2c49f58a33c0a5");
    private static final List<String> SOAKING_HEARTH_PAGE_DIGESTS = List.of(
        "fa03d132a4170f9e7fc200b1cea9a379fce562a25bb87403e6ae0f60fa5e073b",
        "d8587708f102ca5c459c416a7bc4ccbc570176e0c778f5a7a01fb82c1ee6bc65",
        "056b4c143172e76c95de6e408084364c60547ac2df170b9167f47370013a22e4",
        "119ecaade41fdd5f04830607933bf7849879d9ea77152bea6ed9e59ee4d0bab5",
        "4cb2d54403ed86791efb5f9c616f17404f810739f1c3fb1a345d2b8d1d906c63",
        "d188a591c3c9099754ae67a8ecb87b1125367823e33995e35b014fd04ef6fa8c");
    private static final Comparator<GuideData.Entry> BOOK_ORDER = Comparator.comparing(GuideData.Entry::category)
        .thenComparing(GuideData.Entry::chapter, NATURAL).thenComparing(GuideData.Entry::group, NATURAL)
        .thenComparing(GuideData.Entry::title, NATURAL).thenComparing(GuideData.Entry::id);
    private List<GuideData.Entry> entries;
    private int entry, page, scroll, listPage;
    private View view = View.DOMAINS;
    private String domain = "", chapter = "", group = "";
    private Button previousEntry, nextEntry, previousPage, nextPage;
    public GuideScreen(List<GuideData.Entry> entries) { super(Component.translatable("item.arcana_industry.field_guide")); this.entries = ordered(entries); }
    private static List<GuideData.Entry> ordered(List<GuideData.Entry> source) {
        var sorted = new ArrayList<>(source); sorted.sort(BOOK_ORDER); return List.copyOf(sorted);
    }
    /** Case-insensitive order where digit runs compare numerically, so "Tier 2" precedes "Tier 10". */
    static int compareNatural(String a, String b) {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            char x = a.charAt(i), y = b.charAt(j);
            if (Character.isDigit(x) && Character.isDigit(y)) {
                int si = i, sj = j;
                while (i < a.length() && Character.isDigit(a.charAt(i))) i++;
                while (j < b.length() && Character.isDigit(b.charAt(j))) j++;
                String na = a.substring(si, i).replaceFirst("^0+(?=.)", ""), nb = b.substring(sj, j).replaceFirst("^0+(?=.)", "");
                int order = na.length() != nb.length() ? Integer.compare(na.length(), nb.length()) : na.compareTo(nb);
                if (order != 0) return order;
            } else {
                int order = Character.compare(Character.toLowerCase(x), Character.toLowerCase(y));
                if (order != 0) return order;
                i++; j++;
            }
        }
        int order = Integer.compare(a.length() - i, b.length() - j);
        return order != 0 ? order : a.compareTo(b);
    }
    private boolean inPath(GuideData.Entry e, View level) {
        return (level.ordinal() <= View.DOMAINS.ordinal() || e.category().equals(domain))
            && (level.ordinal() <= View.CHAPTERS.ordinal() || e.chapter().equals(chapter))
            && (level.ordinal() <= View.GROUPS.ordinal() || e.group().equals(group));
    }
    /** Distinct labels of one contents level inside the current path, in book order. */
    private List<String> labels(View level) {
        var names = new LinkedHashSet<String>();
        for (var e : entries) if (inPath(e, level)) names.add(switch (level) {
            case DOMAINS -> e.category(); case CHAPTERS -> e.chapter(); case GROUPS -> e.group(); default -> e.title();
        });
        return new ArrayList<>(names);
    }
    public static void receive(GuideNetwork.Snapshot snapshot) {
        Minecraft game = Minecraft.getInstance();
        if (snapshot.open()) game.setScreen(new GuideScreen(snapshot.entries()));
        else if (game.screen instanceof GuideScreen screen) {
            screen.replaceEntries(snapshot.entries());
        }
    }
    private void replaceEntries(List<GuideData.Entry> updated) {
        String selected = entries.isEmpty() ? "" : entries.get(entry).id();
        String previousPage = entries.isEmpty() ? "" : entries.get(entry).pages().get(page);
        entries = ordered(updated);
        int found = -1;
        for (int i = 0; i < entries.size(); i++) if (entries.get(i).id().equals(selected)) { found = i; break; }
        if (found < 0) { entry = 0; page = 0; scroll = 0; showContents(View.DOMAINS); return; }
        if (view != View.READING && view != View.DOMAINS && labels(view).isEmpty()) showContents(View.DOMAINS);
        else if (view != View.READING) rebuild();
        entry = found;
        page = Math.min(page, entries.get(entry).pages().size() - 1);
        // Keep the reader's place when unrelated entries change. Changed text starts at its top.
        if (!previousPage.equals(entries.get(entry).pages().get(page))) scroll = 0;
        updateButtons();
    }
    @Override protected void init() {
        previousEntry = addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.previous_entry"), b -> moveEntry(-1)).bounds(width / 2 - 150, height - 30, 70, 20).build());
        nextEntry = addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.next_entry"), b -> moveEntry(1)).bounds(width / 2 - 75, height - 30, 70, 20).build());
        previousPage = addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.previous_page"), b -> movePage(-1)).bounds(width / 2 + 5, height - 30, 70, 20).build());
        nextPage = addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.next_page"), b -> movePage(1)).bounds(width / 2 + 80, height - 30, 70, 20).build());
        int left = Math.max(10, width / 2 - 170), right = Math.min(width - 10, width / 2 + 170);
        addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.contents"), b -> showContents(View.DOMAINS)).bounds(right - 64, 18, 58, 16).build());
        if (view != View.READING) addContentButtons(left, right);
        updateButtons();
    }
    private static String display(String raw) {
        String slug = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
        String key = "guide.arcana_industry.label." + slug;
        return I18n.exists(key) ? I18n.get(key) : raw;
    }

    private void addContentButtons(int left, int right) {
        List<String> names = labels(view);
        int columnWidth = (right - left - 30) / 2, rows = Math.max(1, (height - 120) / 22), perPage = rows * 2;
        int pages = Math.max(1, (names.size() + perPage - 1) / perPage);
        listPage = Math.floorMod(listPage, pages);
        for (int i = listPage * perPage; i < Math.min(names.size(), (listPage + 1) * perPage); i++) {
            String name = names.get(i); int slot = i - listPage * perPage;
            int x = left + 10 + (slot / rows) * (columnWidth + 10), y = 60 + (slot % rows) * 22;
            String label = display(name);
            String shown = font.width(label) > columnWidth - 8 ? font.plainSubstrByWidth(label, columnWidth - 8 - font.width("...")) + "..." : label;
            Button button = Button.builder(Component.literal(shown), b -> choose(name)).bounds(x, y, columnWidth, 20).build();
            if (!shown.equals(label)) button.setTooltip(Tooltip.create(Component.literal(label)));
            addRenderableWidget(button);
        }
        if (pages > 1) {
            addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.previous_list"), b -> { listPage--; rebuild(); }).bounds(left + 10, height - 56, 60, 16).build());
            addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.next_list"), b -> { listPage++; rebuild(); }).bounds(right - 70, height - 56, 60, 16).build());
        }
        if (view != View.DOMAINS)
            addRenderableWidget(Button.builder(Component.translatable("guide.arcana_industry.back"), b -> showContents(View.values()[view.ordinal() - 1])).bounds(left + 6, 18, 58, 16).build());
    }
    /** Domain, chapter and group buttons descend one level; an entry title opens that entry. */
    private void choose(String name) {
        switch (view) {
            case DOMAINS -> { domain = name; showContents(View.CHAPTERS); }
            case CHAPTERS -> { chapter = name; showContents(View.GROUPS); }
            case GROUPS -> { group = name; showContents(View.ENTRIES); }
            default -> {
                for (int i = 0; i < entries.size(); i++) if (inPath(entries.get(i), View.ENTRIES) && entries.get(i).title().equals(name)) {
                    entry = i; page = 0; scroll = 0; view = View.READING; rebuild(); return;
                }
            }
        }
    }
    private void showContents(View target) { view = target; listPage = 0; rebuild(); }
    private void read() { if (view != View.READING && !entries.isEmpty()) { view = View.READING; rebuild(); } }
    private void rebuild() { if (minecraft != null) { clearWidgets(); init(); } }
    private void updateButtons() {
        if (previousEntry == null) return;
        previousEntry.active = nextEntry.active = entries.size() > 1;
        previousPage.active = nextPage.active = !entries.isEmpty() && entries.get(entry).pages().size() > 1;
    }
    private void moveEntry(int delta) { read(); if (!entries.isEmpty()) entry = Math.floorMod(entry + delta, entries.size()); page = 0; scroll = 0; updateButtons(); }
    private void movePage(int delta) { read(); if (!entries.isEmpty()) page = Math.floorMod(page + delta, entries.get(entry).pages().size()); scroll = 0; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        renderBackground(graphics);
        int left = Math.max(10, width / 2 - 170), right = Math.min(width - 10, width / 2 + 170);
        graphics.fill(left, 15, right, height - 38, 0xffeadfbd);
        graphics.drawCenteredString(font, title, width / 2, 24, 0x403020);
        if (entries.isEmpty()) graphics.drawString(font, Component.translatable("guide.arcana_industry.empty"), left + 10, 55, 0x403020, false);
        else if (view != View.READING) {
            String heading = switch (view) {
                case DOMAINS -> Component.translatable("guide.arcana_industry.choose_part").getString();
                case CHAPTERS -> Component.translatable("guide.arcana_industry.choose_chapter", display(domain)).getString();
                case GROUPS -> Component.translatable("guide.arcana_industry.choose_category", display(domain), display(chapter)).getString();
                default -> Component.translatable("guide.arcana_industry.choose_entry", display(chapter), display(group)).getString();
            };
            drawLabel(graphics, heading, left + 10, 42, right - left - 20, 0x65543d, mouseX, mouseY);
        } else {
            var current = entries.get(entry);
            drawLabel(graphics, display(current.category()) + " / " + display(current.chapter()) + " / " + display(current.group()), left + 10, 42, right - left - 20, 0x65543d, mouseX, mouseY);
            String counter = " (" + (page + 1) + "/" + current.pages().size() + ")";
            drawLabel(graphics, display(current.title()), left + 10, 56, right - left - 20 - font.width(counter), 0x403020, mouseX, mouseY);
            graphics.drawString(font, counter, right - 10 - font.width(counter), 56, 0x403020, false);
            ResourceLocation diagram = diagramFor(current.id(), page);
            boolean hasDiagram = diagram != null && minecraft.getResourceManager().getResource(diagram).isPresent();
            if (hasDiagram) graphics.blit(diagram, left + 10, 76, 0, 0, 96, 96, 96, 96);
            int textLeft = left + (hasDiagram ? 112 : 10);
            var lines = font.split(localizedPage(current, page), right - textLeft - 10);
            int visible = Math.max(1, (height - 120) / 10);
            scroll = Math.max(0, Math.min(scroll, Math.max(0, lines.size() - visible)));
            for (int i = scroll; i < Math.min(lines.size(), scroll + visible); i++) graphics.drawString(font, lines.get(i), textLeft, 76 + (i - scroll) * 10, 0x403020, false);
        }
        super.render(graphics, mouseX, mouseY, partial);
    }
    static Component localizedPage(GuideData.Entry entry, int page) {
        String original = entry.pages().get(page);
        String name;
        List<String> digests;
        if (entry.id().equals("arcana_industry:entries/crusher")) {
            name = "crusher";
            digests = CRUSHER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/kiln")) {
            name = "kiln";
            digests = KILN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/sieve")) {
            name = "sieve";
            digests = SIEVE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/fiber_carder")) {
            name = "fiber_carder";
            digests = FIBER_CARDER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/mold_caster")) {
            name = "mold_caster";
            digests = MOLD_CASTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/sawmill")) {
            name = "sawmill";
            digests = SAWMILL_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/refractory_mixer")) {
            name = "refractory_mixer";
            digests = REFRACTORY_MIXER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/leaching_vat")) {
            name = "leaching_vat";
            digests = LEACHING_VAT_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/ore_sluice")) {
            name = "ore_sluice";
            digests = ORE_SLUICE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/coal_forge")) {
            name = "coal_forge";
            digests = COAL_FORGE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/char_retort")) {
            name = "char_retort";
            digests = CHAR_RETORT_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/enamel_kiln")) {
            name = "enamel_kiln";
            digests = ENAMEL_KILN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/heliostat_etcher")) {
            name = "heliostat_etcher";
            digests = HELIOSTAT_ETCHER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/solar_smelter")) {
            name = "solar_smelter";
            digests = SOLAR_SMELTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/corona_collector")) {
            name = "corona_collector";
            digests = CORONA_COLLECTOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/noctilucent_condenser")) {
            name = "noctilucent_condenser";
            digests = NOCTILUCENT_CONDENSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/stellar_crucible")) {
            name = "stellar_crucible";
            digests = STELLAR_CRUCIBLE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/astral_relay")) {
            name = "astral_relay";
            digests = ASTRAL_RELAY_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lens_grinder")) {
            name = "lens_grinder";
            digests = LENS_GRINDER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prism_scorer")) {
            name = "prism_scorer";
            digests = PRISM_SCORER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prism_polisher")) {
            name = "prism_polisher";
            digests = PRISM_POLISHER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prism_lapidary")) {
            name = "prism_lapidary";
            digests = PRISM_LAPIDARY_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prism_separator")) {
            name = "prism_separator";
            digests = PRISM_SEPARATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prism_tumbler")) {
            name = "prism_tumbler";
            digests = PRISM_TUMBLER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prism_burnisher")) {
            name = "prism_burnisher";
            digests = PRISM_BURNISHER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/arc_press")) {
            name = "arc_press";
            digests = ARC_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/broach_press")) {
            name = "broach_press";
            digests = BROACH_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/assay_hearth")) {
            name = "assay_hearth";
            digests = ASSAY_HEARTH_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/drawing_bench")) {
            name = "drawing_bench";
            digests = DRAWING_BENCH_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/induction_coiler")) {
            name = "induction_coiler";
            digests = INDUCTION_COILER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/capacitor_array")) {
            name = "capacitor_array";
            digests = CAPACITOR_ARRAY_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/flux_rectifier")) {
            name = "flux_rectifier";
            digests = FLUX_RECTIFIER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/polarity_inverter")) {
            name = "polarity_inverter";
            digests = POLARITY_INVERTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/degauss_coil")) {
            name = "degauss_coil";
            digests = DEGAUSS_COIL_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/armor_delaminator")) {
            name = "armor_delaminator";
            digests = ARMOR_DELAMINATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/signal_imprinter")) {
            name = "signal_imprinter";
            digests = SIGNAL_IMPRINTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/resonant_cultivator")) {
            name = "resonant_cultivator";
            digests = RESONANT_CULTIVATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/verdant_distiller")) {
            name = "verdant_distiller";
            digests = VERDANT_DISTILLER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/chromatic_kiln")) {
            name = "chromatic_kiln";
            digests = CHROMATIC_KILN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/prismatic_press")) {
            name = "prismatic_press";
            digests = PRISMATIC_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/electrolyzer")) {
            name = "electrolyzer";
            digests = ELECTROLYZER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/magnetizer")) {
            name = "magnetizer";
            digests = MAGNETIZER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/clamping_press")) {
            name = "clamping_press";
            digests = CLAMPING_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/quench_vat")) {
            name = "quench_vat";
            digests = QUENCH_VAT_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/press")) {
            name = "press";
            digests = PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/swage_press")) {
            name = "swage_press";
            digests = SWAGE_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/crimp_press")) {
            name = "crimp_press";
            digests = CRIMP_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/flange_press")) {
            name = "flange_press";
            digests = FLANGE_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/seam_press")) {
            name = "seam_press";
            digests = SEAM_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/copper_hot_press")) {
            name = "copper_hot_press";
            digests = COPPER_HOT_PRESS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/milling_gantry")) {
            name = "milling_gantry";
            digests = MILLING_GANTRY_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/clay_extruder")) {
            name = "clay_extruder";
            digests = CLAY_EXTRUDER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/boiler_turbine")) {
            name = "boiler_turbine";
            digests = BOILER_TURBINE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/firebox_dynamo")) {
            name = "firebox_dynamo";
            digests = FIREBOX_DYNAMO_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/clinker_oven")) {
            name = "clinker_oven";
            digests = CLINKER_OVEN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/astral_font")) {
            name = "astral_font";
            digests = ASTRAL_FONT_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/cinder_husk")) {
            name = "cinder_husk";
            digests = CINDER_HUSK_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/flux_archer")) {
            name = "flux_archer";
            digests = FLUX_ARCHER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/flux_aegis")) {
            name = "flux_aegis";
            digests = FLUX_AEGIS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/flux_totem")) {
            name = "flux_totem";
            digests = FLUX_TOTEM_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/flux_lancet")) {
            name = "flux_lancet";
            digests = FLUX_LANCET_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_welder")) {
            name = "lattice_welder";
            digests = LATTICE_WELDER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_engraver")) {
            name = "lattice_engraver";
            digests = LATTICE_ENGRAVER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/stasis_weaver")) {
            name = "stasis_weaver";
            digests = STASIS_WEAVER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_conflux")) {
            name = "phase_conflux";
            digests = PHASE_CONFLUX_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_modulator")) {
            name = "phase_modulator";
            digests = PHASE_MODULATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_stabilizer")) {
            name = "phase_stabilizer";
            digests = PHASE_STABILIZER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_preserver")) {
            name = "phase_preserver";
            digests = PHASE_PRESERVER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/containment_forge")) {
            name = "containment_forge";
            digests = CONTAINMENT_FORGE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/concordance_altar")) {
            name = "concordance_altar";
            digests = CONCORDANCE_ALTAR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/rust_crawler")) {
            name = "rust_crawler";
            digests = RUST_CRAWLER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/aluminum_ore")) {
            name = "aluminum_ore";
            digests = ALUMINUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/antimony_ore")) {
            name = "antimony_ore";
            digests = ANTIMONY_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/bismuth_ore")) {
            name = "bismuth_ore";
            digests = BISMUTH_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/chromium_ore")) {
            name = "chromium_ore";
            digests = CHROMIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/cobalt_ore")) {
            name = "cobalt_ore";
            digests = COBALT_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/gallium_ore")) {
            name = "gallium_ore";
            digests = GALLIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/germanium_ore")) {
            name = "germanium_ore";
            digests = GERMANIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/hafnium_ore")) {
            name = "hafnium_ore";
            digests = HAFNIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/indium_ore")) {
            name = "indium_ore";
            digests = INDIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/iridium_ore")) {
            name = "iridium_ore";
            digests = IRIDIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lead_ore")) {
            name = "lead_ore";
            digests = LEAD_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/manganese_ore")) {
            name = "manganese_ore";
            digests = MANGANESE_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/molybdenum_ore")) {
            name = "molybdenum_ore";
            digests = MOLYBDENUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/nickel_ore")) {
            name = "nickel_ore";
            digests = NICKEL_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/niobium_ore")) {
            name = "niobium_ore";
            digests = NIOBIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/osmium_ore")) {
            name = "osmium_ore";
            digests = OSMIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/palladium_ore")) {
            name = "palladium_ore";
            digests = PALLADIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/platinum_ore")) {
            name = "platinum_ore";
            digests = PLATINUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/rhenium_ore")) {
            name = "rhenium_ore";
            digests = RHENIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/rhodium_ore")) {
            name = "rhodium_ore";
            digests = RHODIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/ruthenium_ore")) {
            name = "ruthenium_ore";
            digests = RUTHENIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/silver_ore")) {
            name = "silver_ore";
            digests = SILVER_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/tantalum_ore")) {
            name = "tantalum_ore";
            digests = TANTALUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/thallium_ore")) {
            name = "thallium_ore";
            digests = THALLIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/tin_ore")) {
            name = "tin_ore";
            digests = TIN_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/titanium_ore")) {
            name = "titanium_ore";
            digests = TITANIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/tungsten_ore")) {
            name = "tungsten_ore";
            digests = TUNGSTEN_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/vanadium_ore")) {
            name = "vanadium_ore";
            digests = VANADIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/zinc_ore")) {
            name = "zinc_ore";
            digests = ZINC_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/zirconium_ore")) {
            name = "zirconium_ore";
            digests = ZIRCONIUM_ORE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_composer")) {
            name = "lattice_composer";
            digests = LATTICE_COMPOSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_infuser")) {
            name = "lattice_infuser";
            digests = LATTICE_INFUSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_laminator")) {
            name = "lattice_laminator";
            digests = LATTICE_LAMINATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_reclaimer")) {
            name = "lattice_reclaimer";
            digests = LATTICE_RECLAIMER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lattice_scriber")) {
            name = "lattice_scriber";
            digests = LATTICE_SCRIBER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_collapser")) {
            name = "phase_collapser";
            digests = PHASE_COLLAPSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_diffuser")) {
            name = "phase_diffuser";
            digests = PHASE_DIFFUSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_effuser")) {
            name = "phase_effuser";
            digests = PHASE_EFFUSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_fermenter")) {
            name = "phase_fermenter";
            digests = PHASE_FERMENTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_inverter")) {
            name = "phase_inverter";
            digests = PHASE_INVERTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_revivifier")) {
            name = "phase_revivifier";
            digests = PHASE_REVIVIFIER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_annealer")) {
            name = "phase_annealer";
            digests = PHASE_ANNEALER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_reclaimer")) {
            name = "phase_reclaimer";
            digests = PHASE_RECLAIMER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_refractor")) {
            name = "phase_refractor";
            digests = PHASE_REFRACTOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_scepter")) {
            name = "phase_scepter";
            digests = PHASE_SCEPTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/phase_sublimator")) {
            name = "phase_sublimator";
            digests = PHASE_SUBLIMATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/diffraction_grate")) {
            name = "diffraction_grate";
            digests = DIFFRACTION_GRATE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/hydraulic_riveter")) {
            name = "hydraulic_riveter";
            digests = HYDRAULIC_RIVETER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/iridescent_basin")) {
            name = "iridescent_basin";
            digests = IRIDESCENT_BASIN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/lens_collimator")) {
            name = "lens_collimator";
            digests = LENS_COLLIMATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/silvering_bath")) {
            name = "silvering_bath";
            digests = SILVERING_BATH_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/rotary_screen")) {
            name = "rotary_screen";
            digests = ROTARY_SCREEN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/solar_photolyzer")) {
            name = "solar_photolyzer";
            digests = SOLAR_PHOTOLYZER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/spectral_filter")) {
            name = "spectral_filter";
            digests = SPECTRAL_FILTER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/spectral_loom")) {
            name = "spectral_loom";
            digests = SPECTRAL_LOOM_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/twin_channel_washer")) {
            name = "twin_channel_washer";
            digests = TWIN_CHANNEL_WASHER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/heraldic_frame")) {
            name = "heraldic_frame";
            digests = HERALDIC_FRAME_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/material_traits")) {
            name = "material_traits";
            digests = MATERIAL_TRAITS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/power_cables")) {
            name = "power_cables";
            digests = POWER_CABLES_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/strata_excavator")) {
            name = "strata_excavator";
            digests = STRATA_EXCAVATOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/stock_hatch")) {
            name = "stock_hatch";
            digests = STOCK_HATCH_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/structure_projector")) {
            name = "structure_projector";
            digests = STRUCTURE_PROJECTOR_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/ore_mite")) {
            name = "ore_mite";
            digests = ORE_MITE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/provision_hearth")) {
            name = "provision_hearth";
            digests = PROVISION_HEARTH_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/quern")) {
            name = "quern";
            digests = QUERN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/reliquary_stand")) {
            name = "reliquary_stand";
            digests = RELIQUARY_STAND_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/sanctum_pylon")) {
            name = "sanctum_pylon";
            digests = SANCTUM_PYLON_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/static_creeper")) {
            name = "static_creeper";
            digests = STATIC_CREEPER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/astral_wand")) {
            name = "astral_wand";
            digests = ASTRAL_WAND_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/ash_bellows")) {
            name = "ash_bellows";
            digests = ASH_BELLOWS_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/foundry_ring")) {
            name = "foundry_ring";
            digests = FOUNDRY_RING_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/ember_dryer")) {
            name = "ember_dryer";
            digests = EMBER_DRYER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/resonance_basin")) {
            name = "resonance_basin";
            digests = RESONANCE_BASIN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/smoke_rack")) {
            name = "smoke_rack";
            digests = SMOKE_RACK_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/blast_column")) {
            name = "blast_column";
            digests = BLAST_COLUMN_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/resonance_infuser")) {
            name = "resonance_infuser";
            digests = RESONANCE_INFUSER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/compaction_spire")) {
            name = "compaction_spire";
            digests = COMPACTION_SPIRE_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/compression_packing")) {
            name = "compression_packing";
            digests = COMPRESSION_PACKING_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/basalt_temperer")) {
            name = "basalt_temperer";
            digests = BASALT_TEMPERER_PAGE_DIGESTS;
        } else if (entry.id().equals("arcana_industry:entries/soaking_hearth")) {
            name = "soaking_hearth";
            digests = SOAKING_HEARTH_PAGE_DIGESTS;
        } else return Component.literal(original);
        if (page >= digests.size()) return Component.literal(original);
        try {
            String digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(original.getBytes(StandardCharsets.UTF_8)));
            if (digest.equals(digests.get(page)))
                return Component.translatableWithFallback("guide.arcana_industry." + name + ".page" + (page + 1), original);
        } catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
        return Component.literal(original);
    }
    /** Optional art: `<entry>.png` beside the first page, `<entry>_page<N>.png` beside page N (1-based). */
    private static ResourceLocation diagramFor(String entryId, int page) {
        ResourceLocation id = ResourceLocation.tryParse(entryId);
        if (id == null || !id.getPath().startsWith("entries/")) return null;
        String suffix = page == 0 ? "" : "_page" + (page + 1);
        return new ResourceLocation(id.getNamespace(), "textures/gui/guide/" + id.getPath().substring(8) + suffix + ".png");
    }
    private void drawLabel(GuiGraphics graphics, String text, int x, int y, int available, int color, int mouseX, int mouseY) {
        boolean clipped = font.width(text) > available;
        String shown = clipped ? font.plainSubstrByWidth(text, Math.max(0, available - font.width("..."))) + "..." : text;
        graphics.drawString(font, shown, x, y, color, false);
        if (clipped && mouseX >= x && mouseX < x + available && mouseY >= y && mouseY < y + 10)
            setTooltipForNextRenderPass(Tooltip.create(Component.literal(text)), net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE, true);
    }
    @Override public boolean mouseScrolled(double x, double y, double delta) { scroll -= (int) delta * 3; return true; }
    @Override public boolean isPauseScreen() { return false; }
}
