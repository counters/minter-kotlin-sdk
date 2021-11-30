package counters.minter.sdk.lib

import counters.minter.sdk.minter.Enum.TransactionTypes

class LibTransactionTypes {

    companion object {
        val mapTypeTrs = mapOf(
            TransactionTypes.TypeSend to
                    listOf(
                        "Mt2e466632f7bfeaa2c66253300f8719cec103de650c620220201903711f5736f8",
                        "Mt7e5abc12c069ee4540c50a4558ff7e9500c20f63a62e8262999c457d593d88b2",
                        "Mta58399cc46bfef169a8ea140408814b05b8bc10c820bdfeabe25925d05642008",
                        "Mt9d38dedbf631dd7c5c75dd8cb6c7de31b4e0fda5a948781cafd6e8ee38831c95",
                        "Mt250ce06ae5a95e47db4fdd903699bfa43e44611ec08a93396cd47b8d49db3aff",
                    ),
            TransactionTypes.TypeSellCoin to
                    listOf(
                        "Mta0e6ee85848dd221be2dc83a7f00f0abb51bb2b1dadd2a4a465ae1e6272fe8a2",
                        "Mtc27a2f5c0ca0c70d3808ee0c0ce8f844b2689f827f95113d4e8cd4f352078abe",
                        "Mt46a8c200c0c68dfc4ea49a38da4bdc5ead87a0e9c98213cd716220db51882099",
                        "Mt6ba667ad2340a9152091069b926eaf1efcac4d6abad7e9a1b67f0469a196b7be",
                        "Mt27d0aab954edf6d03c51916c3ed62f3e0a481d956430fbe277dea740b4247147",
                    ),
            TransactionTypes.TypeSellAllCoin to
                    listOf(
                        "Mtaf2dcc33a06f779850bf251a18b837c927a128f6a6b4476aa4d3d3cd8371a9eb",
                        "Mtedae1c1d2ea60433f2653c0ae117e7feec65d136ddfd06a38a7d6f6ef0d38572",
                        "Mt55f3eed04160906a39bde460d612b069dd2a5dbf315cb18749eeab37850a1e53",
                        "Mtc69498e64ea7a22f281279c7215564e1970b720606816bd9a2557673935a5b58",
                        "Mtb5e45b99caf9b34eb59e1a3e7ac56a9ab1c72e822bba0cbef4d1e37cf1558ed2",
                    ),
            TransactionTypes.TypeBuyCoin to
                    listOf(
                        "Mt1cdb7c296a78c876c0b6a32560d6c19c611099485a85dafa991146cb90c1bd9d",
                        "Mtbf91fcb2dbde62f30f73229e51103cad24b5ea264c1a07c2ae4d8c582b2a789f",
                        "Mt8892ee32fe0fdbb3874b449c4fa2740373ba17848764664f2e950da9f4477fd6",
                        "Mt6e1d45d3a46e32fc81443028ef8531ff22c047ac6413828902c727555a111992",
                        "Mtd6299f71d53e54eb49be61ed25cb59bb1a2245eece5e01b3c48f804496dc95a3",
                    ),
            TransactionTypes.TypeCreateCoin to
                    listOf(
                        "Mt7cc11f5806f9ddee7e3ca0f050e939242dc8f7468d0222fc29c55de394a498d6",
                        "Mta8b368ba68ba6f9758f9f8a26fc70fee664b80d587096f8254ebe4465fd2c829",
                        "Mt833709c5de52afbcdfb8c002a57b66882d58394a19569313d23856c1a9d572e8",
                        "Mtc7514332318e93d77ed8a83f8d21ddb6176867cc05de85f46ea06adc597c8ae7",
                        "Mt188283ecd90356c08bac35283ccbc28a3a23a0b5232fef3b2a3a1ea38e13a34d",
                    ),
            TransactionTypes.TypeDeclareCandidacy to
                    listOf(
                        "Mtf24590311d277cebfb4d1d00d1f08786e83c1244f038fe29774200b170651a93",
                        "Mte7409a35819d8bd27e1018e224dd693e8406325cfc781421a582373e06e5c50b",
                        "Mt1a009e372ca951b1aff11c8615ab193fa0702b9974796d6ee44853456997eb99",
                        "Mta000df0faf96c4dbd2f104f037eaca45503c8145fa3752566acb872ac0c80a49",
                        "Mt205823dcccc5a2a61a607a0318f7460e7e83966bb801e29021ae3337d7080ece",
                    ),
            TransactionTypes.TypeDelegate to
                    listOf(
                        "Mt3fbeb3741bd7c37c48624f37863323b3843cda0bfe73d2875d99f1c758bbdf38",
                        "Mt2f44dc025f8a16932bbef54b50c3a00b5375b7edc92c9d45fc0d77459bf58f0f",
                        "Mt6eb2a58724c8c7b52bcce8f87847a4c84f25a606c64bfcdd40968931751c9a45",
                        "Mtf7f8afaa23f37e71d8891ca7df28b09f8b18560bf0df5a0b2e4d205645d08193",
                        "Mt68849df448d0c7ce384b25f1b5030fcbcb64e7da21890b8b1ef54f4406f6b2d9",
                    ),
            TransactionTypes.TypeUnbond to
                    listOf(
                        "Mt3094a6fda799df0078153ce78016a828fe9eb18f28bd7e9ed53197b0b4ce9329",
                        "Mt2622b2b2adc0d1ff7b842c3e8bb78a4c7fbd2c00b89ba6f6c9c946fa573306cf",
                        "Mt62f716b5a5b7ce4501734a88afdafd9dfc696d19c66676fd52af2f9f38cdc488",
                        "Mt676bf11ca4b84cc7e3e75f51f64d0a4c0bf43f5a5aca286bf217974e4ec06427",
                        "Mtf4cec471c37f7008e493e58803f6ca5aa8e494872e376ad7bc1748d8be21050a",
                    ),
            TransactionTypes.TypeRedeemCheck to
                    listOf(
                        "Mt7b7ed67c49bcb3ce57df3cb6b8ad77840cd577d8fe65d0fa24902fcc20baec17",
                        "Mt5e60d923aff4a21d5bcd02d5db3e1649baa03b0fee787313e4efd53c745dbaf7",
                        "Mtc4f39e4c55b45266d58d23778108b0d6a1ab1bb110ba68944ef10039eba15e37",
                        "Mt7834066d34852641512958ea8aa50f7c46696e4e9a5251172f8d43c5559ed40e",
                        "Mtd0bba36564c4daf485ac9f33ec000b937f017238f7ed21803b11f9427f6b3b73",
                    ),
            TransactionTypes.TypeSetCandidateOnline to
                    listOf(
                        "Mt07dd03af7c862ff917b6f536c9117b1ba8b8c40f35a7ed3241dd8dbc99aae925",
                        "Mt79e1fa2dbd4423b0e6fe306eaa841921232c2769e087acfc696dc1ca8b4204ed",
                        "Mtc07b8ee008aa40f04b8ba9192437d67444567e74580bdcd42b462629272f03dd",
                        "Mt1be4d13e4dc507187d0666b84d20284398a5d944662ca16456c1da044a899ca9",
                        "Mt154d70fd9f3e89463de0260a08df7443534573d7715b94291d325fa6f9509f17",
                    ),
            TransactionTypes.TypeSetCandidateOffline to
                    listOf(
                        "Mt6f9244ed0b6bc992c76e0ecec2a9a2c256fdf2a63014a800da6d457ac7675756",
                        "Mtbfdef07277c102c4b1311f461e386fb10bbbe9b42b240aaff6737dc637c09b61",
                        "Mt6bcd839b6709f9ab8030eb2cbccdb85a172acf8f0dcfb044399653971e43900f",
                        "Mtf5d30478f48b333e89a7675fe77466a83767e526ba5978c327bb6ce1dc1855b3",
                        "Mtf5de991737dcd8d2c4f55e92f8be28329635325b12371f0a8a7aee40753e0261",
                    ),
            TransactionTypes.TypeCreateMultisig to
                    listOf(
                        "Mte0244c27fd24614fbc33e9e30686df22f77d449d4fe860d5ecdac4bf27b3a04b",
                        "Mt5796f58ff35420c6a4cc8c8a8b8972f2f42a77d303705fa1acc59e1459f1b838",
                        "Mtbf1e6ad0c42cd3db00ac0fcf559b45caad9a48ff05f80a4f1ce909ec799bc1ad",
                        "Mt2cb1befd53c7be2ba4e64cb03dac5b8e5a14db635a7c0456abcb590560ac69b8",
                        "Mt231e6f4a8202cab904cdffcd3f06efa2fd5d95e7290a0eefa6e333f32714146e",
                    ),
            TransactionTypes.TypeMultiSend to
                    listOf(
                        "Mt9b35f18d6455e860742f898d5887a75efb2727e3e855c35e43acb4e096fc788c",
                        "Mtceee4b3e76378a209dac182039fbb8cd4df0778909994d4097e75545217ae273",
                        "Mt0470b8b85700fcaf32164c6f2b4d3567216d6df086c4631596562c76c9fbfd3c",
                        "Mt3f261825c253917f6ab96b40793745347600e0708dcbf380c6580bee784aae82",
                        "Mta3452f1f80de3dd65ebf5ceaa0758825f9a2b87274196fc6a6a59ef3c2ed4e91",
                    ),
            TransactionTypes.TypeEditCandidate to
                    listOf(
                        "Mt85d1f5d49bcf6ce877ce8651c350d9140f39f54a2eda2defc65960ed64ec2051",
                        "Mt97478384a042f038acf631b10097e367f58eabdf205b0cba9989336438634303",
                        "Mt3dc17a0992c9fcf067ee1f5ec50d134a82a81ef4a239d637bfef82abcb0808a5",
                        "Mt0afa52c947c4a2b7e78d6f2b460da4c0cb4b194a81e3d99871da69029790cd6e",
                        "Mte7f6077246f71457821c7ff5e89ee8a5bc7b9bb9b05e46be32758e5cac9a2873",
                    ),
            TransactionTypes.TypeSetHaltBlock to
                    listOf(
                        "Mtfaf29158597c717130b9bb3180b09ec1ebba37fa7a2d3c5a44fb0e8a80d3b533",
                        "Mtf71478c344171725208b9b7e2073fee3b471697719439123df6de489a5f15220",
                        "Mt397f98aa06f4c196daa20552fcf1aca5b942b746c3682249f0aec1b9f0ebce5d",
                        "Mt26ddd492734bc301cb8302019fb95dcf1b8167236f09b4b01792b01112ae054a",
                        "Mt3154d4d71576d92566a6382f5e93d3a4606bd7d9145e98364b4ceb0e5ef4b199",
                    ),
            TransactionTypes.TypeRecreateCoin to
                    listOf(
                        "Mtb62571b2d7b9fd1b7607850c5434d9cece4a301109890f8550d9cf0970236d64",
                    ),
            TransactionTypes.TypeEditCoinOwner to
                    listOf(
                        "Mt2e1428e6ad647c84c0af3f2956187566431208da4f45b9eb071542fd4b638a36",
                        "Mt0c8cd7d2239191f216e16da042783737ff9f8a1e0ed411cd465234a585e6a933",
                        "Mtb1de9c7f8e43d3baec52ad8e60dded64af1f1cdcd68618a170cc922668486c7b",
                        "Mt3d8489a69f9119eeb037381749c2f100a8a5a0a9b8d673109d36470751a4e058",
                        "Mtf41403448ac437474d99e1b38c87c6131976d73ad26bd55e7e23504081cd3aa2",
                    ),
            TransactionTypes.TypeEditMultisig to
                    listOf(
                        "Mt23c8bb40d4b0fa99a268bf2fe75618b55a230c13539d3805b980ca78528aa03f",
                        "Mt89c01732b38ecce624ecdceb64255b0ef1f7a15296127733f3f59894d9ab497a",
                        "Mtad01ec78d2f9c9c2ec015e3c23cece6344ab93e03bf93a6bd714e6fbcedd69a6",
                        "Mtdfab30466389476a4ebd33f00044f7b16d344195f3b4768339e84bb01f8793f3",
                        "Mtc00a4e26031de804e3450cd75893d0c53e6ff92ecb8cf37241d373428ed6366a",
                    ),
            TransactionTypes.TypePriceVote to null,
            TransactionTypes.TypeEditCandidatePublicKey to null,
            TransactionTypes.ADD_LIQUIDITY to
                    listOf(
                        "Mt4e79eb6f6025e846ad2aa28e08394fef88ca87e24d9833efa1d4ac523b78d9c3",
                        "Mt993ec37615eb1709025c227da2b09fd84e79c6b4245126262f672469615cd5f8",
                        "Mt59cf0f8c18c716802c52806bcbe0c609b1592e5c3cc00f737fad7c66dad41e21",
                        "Mt9079fd4ede7e024f68c0696a204278998248e54ecc35b13700515d2a85aa3ffe",
                        "Mtca4ff8103a48e03f5ff91d80950c3500d0655b632ef2c2c37fb06a8fd6c7a018",
                    ),
            TransactionTypes.REMOVE_LIQUIDITY to
                    listOf(
                        "Mtce35d1b416354aea25d3abf0bd99497e08f4222607fabb6ca06798ca7b69d133",
                        "Mt6e6a818cc7af0780899cb8e9ff64ca2e82b79c9f0b5435fb4f245c6add3b63bf",
                        "Mta61bbf7fac6764e860a9095908225b0ba6fdb348d47bbc7b04844e2c8f66cfc4",
                        "Mtdad6787d7ad6f503c443759ef2ed8a67a51ec028dcf189fe8a92f6a3cdb12f2d",
                        "Mtfc83b89ed284cd6c3d9a72a5a815260ce02a57357b055bd3238d4011bf8e812e",
                    ),
            TransactionTypes.SELL_SWAP_POOL to
                    listOf(
                        "Mtc00278f5c5d80513dd5208483ea1fe12e3d24bc254496fbeba35ec84560a147e",
                        "Mt7cedd479192bd883905a87875b30c0f5741ae257f6551d335aff85a8c31f7da9",
                        "Mt064b6c93a7356979be8fcd0c0fb933abc00ff8e451ab265ee71971790e515a9c",
                        "Mtc7e66837b11129774f90f58d65fd496316fb82af0b01c063f51ee2c1f093372e",
                        "Mt49521b9e2ed02d7e47164a96e8cb240c7d0e18fafc15393c6cc7d6ef698344fd",
                    ),
            TransactionTypes.BUY_SWAP_POOL to
                    listOf(
                        "Mt671adcec89d527258a2a3a6efc1d8d630193b496de71d6ecef6082bbec152bcc",
                        "Mtb8b6ac06dc69ae542862c4656d2145a02c9fb6f1c5b07cb4279469dfbef4592d",
                        "Mt2bfb9f47e10ba98ff72e7789dbe015d8431d31a73f914cc88e6aff29fd1a54a0",
                        "Mtd15113dd296ccbf1258a5ec360b3b4fae56d26d076f51e981e50b395f3acbf9c",
                        "Mt524de8205535e951a3dc1ef813fa5f209c4c8e52785759bc8605c9d8f89fa111",
                    ),
            TransactionTypes.SELL_ALL_SWAP_POOL to
                    listOf(
                        "Mt12f86565891e563aae2ff5467904619a7fe724fe459b251807f691d37e8be2a8",
                        "Mtae484ce43b97c699779a9750894b81546a57cd469dce0993674fce284825c0ac",
                        "Mt24a9ad23dd9224186f765a2d0401aa220e98836eba6d974fb829980134d408b2",
                        "Mta1b505b6b1fa644cb8d59fd074fbe5c63bddbe9d00d713c9d3fcaeaa100ebe11",
                        "Mtaecce73caa4f6439dcd271f63e36f7ebf0163468a6d36276ba4440280f3aa39b",
                    ),
            TransactionTypes.EDIT_CANDIDATE_COMMISSION to
                    listOf(
                        "Mt6b5aaf84e620f0979b1a4189906b8b9116d5f2eac107e426a56dbc4f4b962640",
                        "Mt6de374bef3b3e5091e2e3702bb57ac976f32614ee57c21d5e12702e3b1d450f1",
                        "Mt2e51e7072dc7cbb391d8b2e44755f1f44c792e7fcc4a5b75a5a962e8fad850ad",
                        "Mt7abbe4927ba530b635aea92ffc460f3ec940588141bf10b18717635c149577e0",
                        "Mte04d6eeed9489cfe57fee555a3ccb3b173b8d6ccb45c7a0a5ffcde7371ac4406",
                    ),
            TransactionTypes.MOVE_STAKE to null,
            TransactionTypes.MINT_TOKEN to
                    listOf(
                        "Mtc8b692e3fc14bf79d8caefe54bf821e7cea8983cfbc403f0c7aed239f50622f6",
                        "Mtc0c797cefb6ea9403414115bd79caefa9afd891cc14765b5b9f12e8f4084b14b",
                        "Mt3ac6c3ef4461804855b7e2e7e84275e841a5d65af773f5586377c2deab29e3ca",
                        "Mt565fb0acdcf5d036be83eade2bdf9e215d9384175eecf61cf81428594ad81f40",
                        "Mtce9cbdb6401c07a9443a168b322cc1d3d54b27d7655297f49c1981c5a1fe47b9",
                    ),
            TransactionTypes.BURN_TOKEN to
                    listOf(
                        "Mt401a9f8b4bbca57df367437b255ef788f985142bac0fdc07c6d816ea3d0bdcf1",
                        "Mtbf49c7c0aada0a7f4a36cc5a942b880695700526a04818380eb69d515fb15549",
                        "Mt8b4bd320b9d07c1f1c96d61a4ac7d12309d0f22a213165ab519ceb0d713aee83",
                        "Mt99d8d2100819487f8a3d350270ee6f702119fbe442df442f43b5fa56a3b63907",
                        "Mtc23c759ae9105e9ecd7feaa90ec66cd1ac2e11c72d3d20ee7b6446593e092817",
                    ),
            TransactionTypes.CREATE_TOKEN to
                    listOf(
                        "Mt9a2a24ddd87855527b20712b183b88c2a0efdaffa95e1831d91ae181abd9e94d",
                        "Mt38b5d0db40fe284d7170e27b9b63d3466e0b599b59410b1345fa76fa24950865",
                        "Mt399b5a22a6d62783d66ee228139a4fe0890a2d6f599210493c72880d8b579062",
                        "Mt83b9a6c3a004d7b8d39fbb417bb987ee0af0b17ec5a9a465b63d08f5c950117d",
                        "Mtadaa4ecc8d382ac785ae023012df77e715f52e1e7230fe0d4cb101ca71fa89ee",
                    ),
            TransactionTypes.RECREATE_TOKEN to
                    listOf(
                        "Mtca08470058041973bcc866f38011311f2f121c1ce069f3dd1f23dd85f305b5c0",
                        "Mt6593966d1c581ab2546d30a64f17123085dd2464de30b9b8fd5b6a764ec4f81c",
                        "Mt6cd630ad5147975224f6000cc4e72d04fccf272c60ea09b8036a45a974b3ff48",
                        "Mt2422c2feaf6dfd81fced95cbee06eabecd45d30103307361f2883bc4197462e0",
                        "Mt7205b3552d358e4f21389acb504d7fb6e386b3f13ab469953b1ebab13a8bfa57",
                    ),
            TransactionTypes.VOTE_COMMISSION to
                    listOf(
                        "Mtc8c14e8adb1da21f8ce7e2096e44213b6b5d8b25f4b569e08dfe31c5b9840252",
                        "Mt7dee40bbe9acbf57cd0bff207b5f203e83e6736131b4e28f6f37c278a1c4a32c",
                        "Mt1b8d0aa01fc750948ca5b9419a00e544e979725c2e21e6841057e37ec60c2c19",
                        "Mte9f7d6c79f62df0cbabe7fef3e157c3ec32449f06c31e40052818218571d45cc",
                        "Mt6b49e2c473116d8547729e28fc5c65574b2f5724e1a8b0c04305234505727376",
                    ),
            TransactionTypes.VOTE_UPDATE to
                    listOf(
                        "Mt91b1506c18e4a2979f6d6dabd632220c4cf9ccdfb0f344dc44c3086d6474e64b",
                        "Mtbcf3ae50f8929f00e73766ea7e378c5b074bc7d1fdf498b3213308ec0e5faf77",
                        "Mt8f19abfe0baf85acd0749f6baa48989a2cbe843398a24fe47391a71aeb5e4766",
                        "Mtf4b65049ed5e840eca7c47095606676ba3103beca1ff3399859653d4d716de2d",
                        "Mte8d0471a24928086453024922214b2b522a684371d5b5654e2aa970d219504a6",
                    ),
            TransactionTypes.CREATE_SWAP_POOL to
                    listOf(
                        "Mt10a753ea159cd2ae5aef2a7ea80b0cd9c88e1bb933d5225d39c07567a9af0a00",
                        "Mt65f587e62928c45923123407f43573091e4cf7e44482df67c408a7eb5940a92c",
                        "Mt94e8b94b3e677c7afa556c05e4006a43de1bb11af86081edecf35ae552df3a46",
                        "Mt3f8610521d1c640753a5c1666415114058215309f9bad728114bd69df3b7fd37",
                        "Mt7fa5a5d179dc8a01c2e5afed69e177745f938fa7ff9f156ab55c47ebde64aaed",
                    ),
        )
    }
}