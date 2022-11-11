package counters.minter.sdk.lib

import counters.minter.sdk.minter.enums.TransactionTypes

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
                        "Mt54760fb48a3f32820aaa8cf6326ffde0e0b8cff252d2fd3db9196531bb875c80",
                        "Mt9a761fd036cd035e26ca7d7d6acef8c4a5b1074f172276a6945319fab8110ed3",
                        "Mt9781c2e7ef1b1dc0ee929fdf546f1e7db1e396b8ec87ddfbd56f82c62465f2f9",
                        "Mt35f1a699346b87a25654157e363d8993429dad9acbbdd9d717b19b3e1930b049",
                        "Mt08bd8e71e6249cd61c60af3aaa5c417c77f204768474d15d420f365b8497f961",
                    ),
            TransactionTypes.TypeSellAllCoin to
                    listOf(
                        "Mt665f5cfc46db87909593011a1ba598c527b09b68a43d28ae85605f84516d04e7",
                        "Mt5d43e54523227bac9e3f03bb82f6ae416fd7fc32ffe4d88044149fad1f814fc1",
                        "Mtc1d07bb06c70ef1d3aeb48ea65451022cb087d87bfa46e7a8b7868f2b019941e",
                        "Mtd3bfd30a67c7ba5bef8e154d67a09fa5ab7069814141f0e90dead93aaf970d76",
                        "Mt1428c2a159d4cab8eae25c81d2c1780d427cb03300d14612f6b0d11f75d25078",
                    ),
            TransactionTypes.TypeBuyCoin to
                    listOf(
                        "Mtd57053dc8e89f0b96720a4cde611becadb46517a4b49bdf8c8010997cfd1f68a",
                        "Mt41ff8abf040a4d8a87cd074cb9c1cd2c15028fd5ebbd834703695fc1986a87a0",
                        "Mtd69a5891af0b3dfed941a681f8dc03f9552838b52b54d69177527baa13218424",
                        "Mt91b8de413944cdafd977cd0379a5c3dcf999c073e26f1c6425b89492e1c33f2a",
                        "Mt139a0cdec9d8ef0cab44d9eb849fc2053835323502d39416c3686ead26b138aa",
                    ),
            TransactionTypes.TypeCreateCoin to
                    listOf(
                        "Mtb28ceab934ed8cfcd322dc3563d109f2471a0506ae5d662aec2d75366a3ab039",
                        "Mt7cc11f5806f9ddee7e3ca0f050e939242dc8f7468d0222fc29c55de394a498d6",
                        "Mta8b368ba68ba6f9758f9f8a26fc70fee664b80d587096f8254ebe4465fd2c829",
                        "Mt833709c5de52afbcdfb8c002a57b66882d58394a19569313d23856c1a9d572e8",
                        "Mtc7514332318e93d77ed8a83f8d21ddb6176867cc05de85f46ea06adc597c8ae7",
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
                        "Mt139f045e60bc9bbac5cc614ac8b2015977eedcb5f1b205513e048f5afbb9215d",
                        "Mt046fab3c91ad3ddcf025f21a688cbb1f3e6de2fe7d762bd8a14d66afc1c3045e",
                        "Mte408cbdfcab10a6b90ef92f17d26a524d05a043c01c0061d9cc9c2f881fd7b4e",
                        "Mt8cfa92fa1117521f63a53204213626d64257c8406141bbcf6ccf85f713f84d13",
                        "Mt8d3c54d52ae85a54d597fc048d9ffc24343082d4f034a6ee1e73d0aef3f93d51",
                    ),
            TransactionTypes.TypeRedeemCheck to
                    listOf(
                        "Mt3ae9a7a19cb53c19892e0a18bbfb22e4964d3c8a39fa7d622b25444c8ae8a1e0",
                        "Mt938eb9f03e7c26c12d31518669f7a9855f0dcca525158238406c2531c72ae3f8",
                        "Mt9f27143dd3b790a89186b2cf883b2753d9f6e82a6424d0e9476eda10f33acd5c",
                        "Mtc7f4a4ec325f015fe5aa731f5ea54bbabb6cd36474292ad586ff20c60b8843ea",
                        "Mtb7f58a5602af628c0e2e3ff20415216921cdffda4ebaa0efa4b620d8f0aae64f",
                    ),
            TransactionTypes.TypeSetCandidateOnline to
                    listOf(
                        "Mt4f39f60fbc19bcaa0cca75e3df9367df22a9e694c3d67ade88e23c1942174f07",
                        "Mt4174198533f798a8d8216d72020eb47d3af47f0f9e1c64357aee12f34b98302c",
                        "Mtfc8cfa5d866ac4686ea2ff5be4f2a6ba5c2d9a23f22b88e5167bb4897e3c32aa",
                        "Mtecec9d6d04223457fad784d20d2e905872128ff1423a81703f54fb4bdeced5be",
                        "Mt65eeb8169fdc62f28b1df72b8dc9174df0efb0e8b06352db4f66ec1e7ed8278c",
                    ),
            TransactionTypes.TypeSetCandidateOffline to
                    listOf(
                        "Mt61217e089196b8be95081db26fa77ef42fd3a96f1fb93f637b5296fc3e8c1846",
                        "Mtf34cfb0ac836189182475d977f8eef27a6b3c76e5d4c7ed83f96e098a7747806",
                        "Mt5bfdd416e63f0cc035560424645edeb3abdc0756503e7092f875fcf58898bcb0",
                        "Mt9c7fdd6491bb14a53e4fa12014ee979979a82aeb203b27f81abbe7687fd4ae09",
                        "Mt9ba7b30972703893e9b9e6f3a64896086ea65bdecc274713b8c4836795d2a1b0",
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
                        "Mt5a7fbaf15fb87abf77dd3ede82717729896b62ff9da37a79c317cc812ba2452a",
                        "Mtc37fa2a66efe84d9b8c4259df2b8a6dc8a61058c678f6656314ce44692c47fca",
                        "Mtae8cd4101937d0004d4e4dc2dd915ea6f9d5d63d806f5dcb8c967d7a37a7bfe6",
                        "Mtf9ea58e6748941862938c4be7c2ff7a21c76aeccddfbd3fbcab12b5de4d3ca1c",
                        "Mt9a747fa0626b455b24faa03a93cdce6e71cf578d9f1d4c106f0e6136e5db91f8",
                    ),
            TransactionTypes.TypePriceVote to null,
            TransactionTypes.TypeEditCandidatePublicKey to null,
            TransactionTypes.ADD_LIQUIDITY to
                    listOf(
                        "Mt00338e9379ba52663ee24fcc517efe81d39baadf1117825d343fe2d8797b0c62",
                        "Mtb2623b21bd2a3b425822be76e6f4200a56aa591eae4b26a79e3548e7cc4d377a",
                        "Mt2d2ad24d6c94c168c9220e5400be13723c8bb22e87a9b6f014ef5dea3fe26492",
                        "Mt523a9d2f02e9f9a5d8f7727ccf8d5346a09f872dd6a884e8c53877a818cf1ca3",
                        "Mt64a93b677fdcd223e470f1435326d20f3d03e35bfaa9e2825a5056bc3447b986",
                    ),
            TransactionTypes.REMOVE_LIQUIDITY to
                    listOf(
                        "Mt9f5a55ce6533baf546d8ddf86899d19529ece36676dcee93b2290cdaab42f772",
                        "Mt0f1a0ab9acc2f45240fcffbfc2fc8d8289880f39e4c46f99e2a0c2135a0f5bf9",
                        "Mt9ac148c2287a4ad6cd022c622c1594dd5cac62b6a6dff424d22b637c7015ae24",
                        "Mte7ae378aa1195f306e507d1b08d2cf4f046126ad900b4317df20cb0b3e92fc13",
                        "Mt2076660ea75ae2c3994d31a2f2d17963bc8037aa41c0bf02147d8ebd25628115",
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
                        "Mtf8e8decafb8080592bdd3d18e4434732eb58e8e30ebb9449e170800d70d8c091",
                        "Mt1a9b28d7aec50ec38c9a219150a6055fbbc869307b39c9f8f64d7d74bf912386",
                        "Mt6377fb4e5dd0dffea29ab72f65bfc6ef8071b7833c22f5dcc73476b31b3ee54b",
                        "Mt7c790fe0e75fa1b8e91f7e8a2a23ac5dc4992d906b94fac79557694cbdf25a35",
                        "Mt669474a6d473ca7d400a356a29b933ea58897c7491903278e4ee1cbf2f491fa5",
                    ),
            TransactionTypes.SELL_ALL_SWAP_POOL to
                    listOf(
                        "Mt660e1110505e752548c1642d803fe62466d52faa204bf7c2e86a38b5c2c12f06",
                        "Mt48c64d8f1cf180e756786ce7d4d9eeab00e1a07560aa33ba93571d50644ff7bc",
                        "Mt6454565ef46ebde111f976dfb6310bc62877f23b696b6223fae803b77e683af1",
                        "Mt57a1ff87d7b4adad0405af8b5627e702e170a0a5a4c568eaf4db52f3ddc775b3",
                        "Mtacf701cf519dd2663c143e1443f1430f86d91d125b3059caec0800cb5b3636a4",
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
                        "Mt28b69025c1435bd2863d6b82e722e0e31eda8eeb14d37b1317e4fa50946c0926",
                        "Mtd9e266aec6dfee304f44a6414d0ea90465fa800e9479c8d05c199336452b4342",
                        "Mtf8ce4994e2dd3082fc247d1b84fea0a7dbead2e9fc8cea2374e34116566e9cca",
                        "Mte08ad79257c2f8a11469b2a1e99753775a3619215e7e19e60ef8d8a4502140a3",
                        "Mt49733e665820b0a15325d55fdfdcabc7329e3e940930e6d39957eb8f39394813",
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
                        "Mt2aacc876c3e804f9a5399f025b2fb657e0997c4919b33d8eb5a8ca7d6c3e4ba9",
                        "Mta3c22a56a36f86b70604d1d01262a20a9d4734668c1cbeae61c30ff92702eff6",
                        "Mtb0799a386b9a891e5ce4a3a039fa342bf989cdef482640130b60eb55305e54d8",
                        "Mt25c290553133f9225a583ee15d6ff4f965c01684b98b77052913eafffc6861ba",
                        "Mt6e9839b9d7d0a58727452748cf5924cf6f26ae3fb41adafdccb4b25e37807a4b",
                    ),
            TransactionTypes.VOTE_UPDATE to
                    listOf(
                        "Mt1e54c4eaaad07660c6ed986a9c27de61f70f3a2dabda0338c688c8af7e15bcf3",
                        "Mt91b1506c18e4a2979f6d6dabd632220c4cf9ccdfb0f344dc44c3086d6474e64b",
                        "Mtbcf3ae50f8929f00e73766ea7e378c5b074bc7d1fdf498b3213308ec0e5faf77",
                        "Mt8f19abfe0baf85acd0749f6baa48989a2cbe843398a24fe47391a71aeb5e4766",
                        "Mtf4b65049ed5e840eca7c47095606676ba3103beca1ff3399859653d4d716de2d",
                    ),
            TransactionTypes.CREATE_SWAP_POOL to
                    listOf(
                        "Mtc25da5cc9764a928204437ef80c23d796edeba72ba4d3cd3e49df6d73e7de76c",
                        "Mtf644b9fba63dde224efed04ec926b72b7ecbc5cc52c65268620bef7b364cf7fa",
                        "Mt22b6c377501a1fd92e32fbd38435940070994683ee697af7f112adf024112b08",
                        "Mt76bb4599633f8f31f8d1c1710db2dc9fb5822e27c8132ae434bf43c943252461",
                        "Mt10a753ea159cd2ae5aef2a7ea80b0cd9c88e1bb933d5225d39c07567a9af0a00",
                    ),
            TransactionTypes.ADD_LIMIT_ORDER to
                    listOf(
                        "Mt22b25c1af829cfed2c72406a5ab8e0be92adeb372ad3c58c7a53c12aae44a33b",
                        "Mte95e96c7bab7303a168b8984d996a5fa61f271ac66c4291188290ba6f38a2054",
                        "Mt780917f9dfc4ca412e471901c5db74a4b1c10cd314f17cb5828ced02fe568e8e",
                        "Mt0e5f766c899777d19df5fa5a816c6797e0ed61aadb892801962fe3bee19ce0ec",
                        "Mtd60dce8b80231f17aab8cbc38bd955b1586075f942e9671bf224a28054233c95",
                    ),
            TransactionTypes.REMOVE_LIMIT_ORDER to
                    listOf(
                        "Mt842928dea6a37e3c5203ba12bd0a3b0852062ffda5062d0fd8ed52d194b5fdb7",
                        "Mt40710b9f54d4606bb7ff34c0ba40908173c47c584e4b3e1b26005a9cd0daa451",
                        "Mt563b258e4c9d12e03dcdac007340bfeb36917c2c146dbfad613b53996503e201",
                        "Mt2c18e18b74e895a35e7ff9b09500eff95667e23c02321e09884f39877ae5a3b7",
                        "Mt2b94bcb04fddafacb1415fce64e2c202edbea5f5cfbfcef677787750f966091f",
                    ),
        )
    }
}
